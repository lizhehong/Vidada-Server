package com.elderbyte.server.vidada.images;

import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.server.vidada.cache.IImageCache;
import com.elderbyte.server.vidada.cache.ImageFileCache;
import com.elderbyte.server.vidada.cache.LeveledImageCache;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Provides helper methods to build image caches.
 */
@Service
public class ImageCacheFactory {

    private static final Logger logger = LogManager.getLogger(ImageCacheFactory.class.getName());

    private final IRawImageFactory imageFactory;


    @Autowired
    public ImageCacheFactory(IRawImageFactory imageFactory){
        this.imageFactory = imageFactory;
    }


    /**
	 * Build a leveled cache form the given two caches.
     *
	 * @param firstLevelCache
	 * @param secondLevelCache
	 * @return
	 */
	public IImageCache leveledCache(IImageCache firstLevelCache, IImageCache secondLevelCache){
		IImageCache imageCache = null;
		if(firstLevelCache != null){
			if(secondLevelCache != null){
				imageCache = new LeveledImageCache(
						firstLevelCache,
						secondLevelCache
						);
			}else {
				imageCache = firstLevelCache;
			}
		}else if(secondLevelCache != null){
			imageCache = secondLevelCache;
            logger.warn("Can not create leveled cache, since firstLevelCache is NULL!");
		}else{
			// both caches are null
			return null;
		}
		return imageCache;
	}

    /**
     * Opens an non encrypted cache.
     *
     * @param cacheLocation
     * @return
     */
	public IImageCache openCache(DirectoryLocation cacheLocation) {
		return new ImageFileCache(imageFactory, cacheLocation);
	}
}
