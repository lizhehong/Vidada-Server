package com.elderbyte.server.vidada.cache;

import com.elderbyte.server.vidada.images.IMemoryImage;
import com.elderbyte.server.vidada.media.Resolution;

import java.util.Set;

/**
 * Combines two caches
 * @author IsNull
 *
 */
public class LeveledImageCache implements IImageCache {

	// Add cached images from second cache to the first one
	private final boolean updateFirstFromSecond = true;

	// Add cached images in the first level cache to the second one
	private final boolean updateSecondFromFirst = true;

	private final IImageCache firstLevelCache;
	private final IImageCache secondLevelCache;

	/**
	 *
	 * @param firstLevelCache
	 * @param secondLevelCache
	 */
	public LeveledImageCache(IImageCache firstLevelCache, IImageCache secondLevelCache) {
		if(firstLevelCache == null) throw new IllegalArgumentException("firstLevelCache");
		if(secondLevelCache == null) throw new IllegalArgumentException("secondLevelCache");

		this.firstLevelCache = firstLevelCache;
		this.secondLevelCache = secondLevelCache;
	}



	@Override
	public IMemoryImage getImageById(String id, Resolution size) {

		IMemoryImage image = firstLevelCache.getImageById(id, size);

		if(image == null){
			// image does not exist in firstLevel cache
			image = secondLevelCache.getImageById(id, size);
			if(updateFirstFromSecond && image != null){
				firstLevelCache.storeImage(id, image);
			}
		}else if(updateSecondFromFirst && !secondLevelCache.exists(id, size)) {
			secondLevelCache.storeImage(id, image);
		}

		return image;
	}

	@Override
	public Set<Resolution> getCachedDimensions(String id) {
		Set<Resolution> fd = firstLevelCache.getCachedDimensions(id);
		Set<Resolution> sd = secondLevelCache.getCachedDimensions(id);
		fd.addAll(sd); // Merge

		return fd;
	}

	@Override
	public boolean exists(String id, Resolution size) {
		return firstLevelCache.exists(id, size) || secondLevelCache.exists(id, size);
	}

	@Override
	public void storeImage(String id, IMemoryImage image) {
		firstLevelCache.storeImage(id, image);
		secondLevelCache.storeImage(id, image);
	}

	@Override
	public void removeImage(String id) {
		firstLevelCache.removeImage(id);
		secondLevelCache.removeImage(id);
	}

}
