package com.elderbyte.vidada.domain.tags.autoTag;



import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.tags.Tag;

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
	Set<Tag> guessTags(MediaItem media);

}