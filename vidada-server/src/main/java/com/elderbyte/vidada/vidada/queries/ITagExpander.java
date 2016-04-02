package com.elderbyte.vidada.vidada.queries;

import com.elderbyte.vidada.vidada.tags.Tag;

import java.util.Set;

/**
 *
 */
public interface ITagExpander {

    Set<Tag> getAllRelatedTags(Tag tag);

}
