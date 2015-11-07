package com.elderbyte.vidada.queries;

import com.elderbyte.vidada.tags.Tag;

import java.util.Set;

/**
 *
 */
public interface ITagExpander {

    Set<Tag> getAllRelatedTags(Tag tag);

}
