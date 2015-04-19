package com.elderbyte.vidada.domain.images;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import com.elderbyte.vidada.domain.media.MediaItem;

/**
 * Generic media thumbnail extractor service.
 */
public interface IThumbImageExtractor {

	/**
	 * Is it possible to extract a thumb for the given media?
	 * @param media
	 * @return
	 */
	boolean canExtractThumb(MediaItem media);

	/**
	 * Extract a thumb from the given media in the specified size
	 *
	 *
	 * @param media
	 * @param size
	 * @return
	 */
	IMemoryImage extractThumb(MediaItem media, Size size);

}
