package com.elderbyte.vidada.tags.relations;

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
