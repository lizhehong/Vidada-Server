package com.elderbyte.server.vidada.tags;

import com.elderbyte.server.vidada.tags.relations.ITagRelationSource;
import com.elderbyte.server.vidada.tags.relations.TagRelationDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Manages all tags and tag-relations of a Vidada Server.
 */
@Service
public class TagService {


    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final TagRepository repository;
    private final List<ITagRelationSource> tagRelationSources = new ArrayList<>();

    private TagRelationDefinition relationDefinition;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new TagService
     * @param repository
     */
    @Autowired
    public TagService(TagRepository repository) {
        this.repository = repository;
    }


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    /**
     * Returns all tags which are equal or include the given tag in a logical matter.
     * As an example:
     *
     * related tags of 'car' could be:
     * - automobile
     * - convertible
     * - sports.car
     * - bmw
     * - audi
     *
     * All these tags _are_ a 'car'.
     *
     * @param tag
     * @return
     */
    public Set<Tag> getAllRelatedTags(Tag tag){
        return getRelationDefinition().getAllRelatedTags(tag);
    }


    /**
     * Deletes a tag
     * @param tag
     */
    @Transactional
    public void removeTag(final Tag tag) {
        repository.delete(tag);
    }


    /**
     * Returns all main tags.
     * @return
     */
    @Transactional
    public Collection<Tag> findAllUsedTags() {

        Collection<Tag> allTags = findAllTags();

        // Remove all synonyms from our tag list
        Iterator<Tag> allTagsIt = findAllTags().iterator();
        while (allTagsIt.hasNext()) {
            Tag tag = allTagsIt.next();
            if (getRelationDefinition().isSlaveTag(tag)) {
                allTagsIt.remove();
            }
        }

        return allTags;
    }

    /**
     * Returns all known tags
     * @return
     */
    @Transactional
    public Collection<Tag> findAllTags() {

        // Ensure all tags from the relations are loaded and known
        getRelationDefinition();

        return repository.findAll();
    }

    /**
     * Finds or creates a tag with the given name.
     * The name is turned into a valid tag-name if necessary.
     * @param tagName
     * @return
     */
    @Cacheable("tags")
    public Tag findOrCreateTag(String tagName) {

        Tag tag = Tag.buildTag(tagName).orElse(null);

        if(tag != null){
            Tag existing = repository.findOne(tag.getName());
            if(existing == null){
                repository.save(tag);
            }
        }else{
            logger.warn("Could not create new tag from name '" + tagName + "'!");
        }
        return tag;
    }


    /**
     * Invalidates relation definitions and internal indexes.
     * Next time the relations are required, they are rebuilt from the sources.
     */
    public void invalidateTagRelations() {
        relationDefinition = null;
    }

    /**
     * Registers a source for tag-relations.
     * @param relationSource
     */
    public synchronized void registerTagRelationSource(ITagRelationSource relationSource){
        tagRelationSources.add(relationSource);
        logger.info("TagRelationSource has been added!");
        invalidateTagRelations();
    }


    public Set<Tag> parseTags(String tagString) {
        Set<Tag> parsedTags = new HashSet<>();
        String[] tags = tagString.split("[,|\\|]");

        String tagName;
        for (String t : tags) {
            tagName = t.trim();
            if (!tagName.isEmpty()) {
                Optional<Tag> newTag = Tag.buildTag(tagName);
                if (newTag.isPresent())
                    parsedTags.add(newTag.get());
            }
        }
        return parsedTags;
    }




    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/




    private synchronized TagRelationDefinition getRelationDefinition(){

        if(relationDefinition == null){
            relationDefinition = buildRelationDefinition();
        }
        return relationDefinition;
    }

    private TagRelationDefinition buildRelationDefinition() {

        logger.info(String.format("Rebuilding TagRelationDefinition from all registered %s sources...", tagRelationSources.size()));

        TagRelationDefinition relationDefinition = new TagRelationDefinition();

        for (ITagRelationSource relationSource : tagRelationSources){
            TagRelationDefinition definition = relationSource.buildTagRelation();
            if(definition != null){
                relationDefinition.merge(definition);
            }
        }

        ensureTagsExist(relationDefinition.getAllTags());
        // ensure that all known tags are in the Database
        return relationDefinition;
    }

    @Transactional
    void ensureTagsExist(final Collection<Tag> tags){
        for (Tag tag : tags) {
            findOrCreateTag(tag.getName());
        }
    }


}
