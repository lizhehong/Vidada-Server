package com.elderbyte.vidada.service.tags;

import com.elderbyte.vidada.domain.tags.Tag;
import com.elderbyte.vidada.domain.tags.TagUtil;
import com.elderbyte.vidada.domain.tags.relations.TagRelationDefinition;
import com.elderbyte.vidada.repository.TagRepository;
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
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/



    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/



    public Set<Tag> getAllRelatedTags(Tag tag){
        return getRelationDefinition().getAllRelatedTags(tag);
    }



    @Transactional
    public void removeTag(final Tag tag) {
        repository.delete(tag);
    }


    @Transactional
    public Collection<Tag> getUsedTags() {

        Collection<Tag> allTags = getAllTags();

        // Remove all synonyms from our tag list
        Iterator<Tag> allTagsIt = getAllTags().iterator();
        while (allTagsIt.hasNext()) {
            Tag tag = allTagsIt.next();
            if (getRelationDefinition().isSlaveTag(tag)) {
                allTagsIt.remove();
            }
        }

        return allTags;
    }

    @Transactional
    public Collection<Tag> getAllTags() {

        // Ensure all tags from the relations are loaded and known
        getRelationDefinition();

        return repository.findAll();
    }

    @Cacheable("tags")
    public Tag getTag(String tagName) {
        tagName = TagUtil.toTagString(tagName);

        Tag tag = repository.findOne(tagName);

        if(tag == null){
            Optional<Tag> tagOpt = TagUtil.createTag(tagName);
            if(tagOpt.isPresent()){
                tag = tagOpt.get();
                repository.save(tag);
            }else{
                logger.warn("Could not create new tag from name '" + tagName + "'!");
            }
        }

        return tag;
    }

    /**
     * Update relation definition
     */
    public void invalidateTagRelations() {
        relationDefinition = null;
    }

    public synchronized void registerTagRelationSource(ITagRelationSource relationSource){
        tagRelationSources.add(relationSource);
        logger.info("TagRelationSource has been added!");
        invalidateTagRelations();
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
            getTag(tag.getName());
        }
    }


}
