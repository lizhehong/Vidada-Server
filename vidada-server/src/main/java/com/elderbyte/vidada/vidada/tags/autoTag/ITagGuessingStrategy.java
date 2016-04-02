package com.elderbyte.vidada.vidada.tags.autoTag;


import com.elderbyte.vidada.vidada.media.MediaItem;

import java.util.Set;

/**
 * Implements the AutoTag feature, so that for each media item
 * @author IsNull
 *
 */
public interface ITagGuessingStrategy {

	/**
	 * Guess all tags for this media item
	 * @param media
	 * @return Returns a set of tags which apply to this media
	 */
	Set<String> guessTags(MediaItem media);

}
