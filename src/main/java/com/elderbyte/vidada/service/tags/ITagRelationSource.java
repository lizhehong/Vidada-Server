package com.elderbyte.vidada.service.tags;

import com.elderbyte.vidada.domain.tags.relations.TagRelationDefinition;

/**
 * Represents a source for tag relations
 */
public interface ITagRelationSource {

    /**
     * Build the tag-relation of this source.
     * @return
     */
    TagRelationDefinition buildTagRelation();

}
