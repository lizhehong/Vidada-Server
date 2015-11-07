package com.elderbyte.vidada.cache;


import archimedes.core.images.IMemoryImage;
import com.elderbyte.vidada.media.Resolution;

import java.util.Set;

/**
 * Represents an image cache.
 *
 * Each image is identified by its unique id AND the size.
 *
 * @author IsNull
 *
 */
public interface IImageCache {

	/**
	 * Returns the image from cache
	 * @param id The id of the image
	 * @param size The size of the image
	 * @return
	 */
	IMemoryImage getImageById(String id, Resolution size);

	/**
	 * For the given id, returns all available image dimensions
	 * @param id The id of the image
	 * @return
	 */
	Set<Resolution> getCachedDimensions(String id);

	/**
	 * Does the given image exist in this cache?
	 * @param id
	 * @param size
	 * @return
	 */
	boolean exists(String id, Resolution size);

	/**
	 * Persist the given image in this cache
	 * @param id
	 * @param image
	 */
	void storeImage(String id, IMemoryImage image);


	/**
	 * Remove the given image from this cache
	 * @param id
	 */
	void removeImage(String id);
}
