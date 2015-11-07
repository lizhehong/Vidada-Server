package com.elderbyte.vidada.tags.autoTag;

import com.elderbyte.vidada.media.MediaItem;
import com.elderbyte.vidada.tags.Tag;

import java.util.Set;

public class AutoTagSupport {

	/**
	 * Updates the media item with tags according to its filename
	 * @param media
	 * @return Returns true if there where any tags added to the media
	 */
	public static boolean updateTags(ITagGuessingStrategy strategy, MediaItem media)
	{
		boolean tagsUpdated = false;
        int tagCountBefore = media.getTags().size();
        Set<Tag> allGuessedTags = strategy.guessTags(media);

		if(!allGuessedTags.isEmpty())
		{
			media.getTags().addAll(allGuessedTags);
            // Check if we have more tags, then we have changes
			tagsUpdated = tagCountBefore != media.getTags().size();
		}

		return tagsUpdated;
	}
}
