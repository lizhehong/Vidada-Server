package com.elderbyte.vidada.domain.tags;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides helper methods to create Tag instances including a simple cache.
 *
 * @author IsNull
 *
 */
public class TagFactory {

	// --- Singleton

	private TagFactory() {}

	private static TagFactory instance;

	public synchronized static TagFactory instance(){
		if(instance == null){
			instance = new TagFactory();
		}
		return instance;
	}

	// Singleton end ---

	public Set<Tag> parseTags(String tagString) {
		Set<Tag> parsedTags = new HashSet<Tag>();
		String[] tags = tagString.split("[,|\\|]");

		String tagName;
		for (String t : tags) {
			tagName = t.trim();
			if(!tagName.isEmpty())
			{
				Tag newTag = createTag(tagName);
				if(newTag != null)
					parsedTags.add( newTag );
			}
		}
		return parsedTags;
	}

	public Set<Tag> createTags(String... tags){
		Set<Tag> createdTags = new HashSet<Tag>();
		for(String tagStr : tags){
			Tag t = createTag(tagStr);
			if(t != null){
				createdTags.add(t);
			}
		}
		return createdTags;
	}

	public Tag createTag(String tagName){
		Tag tag = null;
		tagName = toTagString(tagName);
		if(isValidTag(tagName)) {
			tag = new Tag(tagName);
		}
		return tag;
	}

    public String toTagString(String text){
        // TODO maybe replace special chars here as well?
        return text.trim().toLowerCase();
    }

	public boolean isValidTag(String tagName){
        return(tagName != null && !tagName.isEmpty());
	}

}
