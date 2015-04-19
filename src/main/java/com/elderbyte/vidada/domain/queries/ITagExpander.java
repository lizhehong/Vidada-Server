package com.elderbyte.vidada.domain.queries;

import com.elderbyte.vidada.domain.tags.Tag;

import java.util.Set;

/**
 *
 */
public interface ITagExpander {

    Set<Tag> getAllRelatedTags(Tag tag);

}
