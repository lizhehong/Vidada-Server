package com.elderbyte.vidada.tags;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Provides helper methods to create Tag instances including a simple cache.
 *
 * @author IsNull
 *
 */
public final class TagUtil {


	public static Set<Tag> parseTags(String tagString) {
        Set<Tag> parsedTags = new HashSet<>();
        String[] tags = tagString.split("[,|\\|]");

        String tagName;
        for (String t : tags) {
            tagName = t.trim();
            if (!tagName.isEmpty()) {
                Optional<Tag> newTag = createTag(tagName);
                if (newTag.isPresent())
                    parsedTags.add(newTag.get());
            }
        }
        return parsedTags;
    }

    public static Set<Tag> createTags(String... tags){
		Set<Tag> createdTags = new HashSet<>();
		for(String tagStr : tags){
            Optional<Tag> t = createTag(tagStr);
			if(t.isPresent()){
				createdTags.add(t.get());
			}
		}
		return createdTags;
	}

    /**
     * Creates a new Tag from the given string.
     * @param tagName
     * @return
     */
	public static Optional<Tag> createTag(String tagName){
        Tag tag = null;
		tagName = toTagString(tagName);
		if(isValidTag(tagName)) {
			tag = new Tag(tagName);
		}
		return Optional.ofNullable(tag);
	}

    public static String toTagString(String text){
        // TODO maybe replace special chars here as well?
        return text.trim().toLowerCase();
    }

	public static boolean isValidTag(String tagName){
        return(tagName != null && !tagName.isEmpty());
	}

}
