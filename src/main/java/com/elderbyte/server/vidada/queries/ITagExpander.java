package com.elderbyte.server.vidada.queries;

import com.elderbyte.server.vidada.tags.Tag;

import java.util.Set;

/**
 *
 */
public interface ITagExpander {

    Set<Tag> getAllRelatedTags(Tag tag);

}
