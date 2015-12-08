package com.elderbyte.server.vidada.tags.autoTag;



import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.tags.Tag;

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
