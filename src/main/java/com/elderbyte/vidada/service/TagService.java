package com.elderbyte.vidada.service;

import archimedes.core.util.Lists;
import com.elderbyte.vidada.domain.tags.Tag;
import com.elderbyte.vidada.domain.tags.relations.TagRelationDefinition;
import com.elderbyte.vidada.repository.TagRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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

    private final TagRepository repository;
    private final TagRelationDefinition relationDefinition;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     *
     * @param repository
     */
    @Inject
    public TagService(TagRepository repository) {
        this.repository = repository;
        relationDefinition = new TagRelationDefinition();
    }


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/



    public Set<Tag> getAllRelatedTags(Tag tag){
        return relationDefinition.getAllRelatedTags(tag);
    }



    @Transactional
    public void removeTag(final Tag tag) {
        repository.delete(tag);
    }


    @Transactional
    public Collection<Tag> getUsedTags() {
        List<Tag> allTags = repository.findAll();

        // Remove all synonyms from our tag list
        Iterator<Tag> allTagsIt = allTags.iterator();
        while (allTagsIt.hasNext()) {
            Tag tag = allTagsIt.next();
            if (relationDefinition.isSlaveTag(tag)) {
                allTagsIt.remove();
            }
        }

        return allTags;
    }

    @Transactional
    public Collection<Tag> getAllTags() {
        return repository.findAll();
    }

    @Cacheable("tags")
    public Tag getTag(String tagName) {
        tagName = Tag.FACTORY.toTagString(tagName);

        Tag tag = repository.findOne(tagName);

        if(tag == null){
            tag = Tag.FACTORY.createTag(tagName);
            repository.save(tag);
        }

        return tag;
    }

    /**
     * Merges the given Tag Relations into this Tag-Service.
     * (This will cause this tag service to learn this relations)
     *
     * @param relationDef
     */
    public void mergeRelation(TagRelationDefinition relationDef) {
        relationDefinition.merge(relationDef);
        ensureTagIsInDB(relationDef.getAllTags());
        // ensure that all known tags are in the Database
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    @Transactional
    void ensureTagIsInDB(final Collection<Tag> tags){
        for (Tag tag : tags) {
            getTag(tag.getName());
        }
    }


}
