package com.elderbyte.vidada.service.images;

import archimedes.core.images.IRawImageFactory;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.security.AuthenticationException;
import com.elderbyte.vidada.domain.images.cache.IImageCache;
import com.elderbyte.vidada.domain.images.cache.ImageFileCache;
import com.elderbyte.vidada.domain.images.cache.LeveledImageCache;
import com.elderbyte.vidada.domain.images.cache.crypto.CacheKeyProvider;
import com.elderbyte.vidada.domain.images.cache.crypto.CryptedImageFileCache;
import com.elderbyte.vidada.domain.images.cache.crypto.ICacheKeyProvider;
import com.elderbyte.vidada.domain.security.ICredentialManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


import javax.inject.Inject;
import java.io.File;

/**
 * Provides helper methods to build image caches.
 */
@Service
public class ImageCacheFactory {

    private static final Logger logger = LogManager.getLogger(ImageCacheFactory.class.getName());

    private final IRawImageFactory imageFactory;


    @Inject
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
     * Opens a encrypted image cache at the given file location.
     * The concrete used cache implementation depends on the location type.
     *
     * @param cacheLocation
     * @param credentialManager
     * @return
     */
    public IImageCache openEncryptedCache(File cacheLocation, ICredentialManager credentialManager) {
        File absCacheLocation = new File(cacheLocation.getAbsolutePath());
        return openEncryptedCache(
                DirectoryLocation.Factory.create(absCacheLocation),
                credentialManager);
    }


    /**
     * Opens a encrypted image cache at the given file location.
     * The concrete used cache implementation depends on the location type.
     *
     * @param cacheLocation
     * @param credentialManager
     * @return
     */
	public IImageCache openEncryptedCache(DirectoryLocation cacheLocation, ICredentialManager credentialManager) {
		ICacheKeyProvider cacheKeyProvider = new CacheKeyProvider(credentialManager);
		IImageCache cache = null;
		try {
			cache = new CryptedImageFileCache(imageFactory, cacheLocation, cacheKeyProvider);
		} catch (AuthenticationException e) {
            logger.error("AuthenticationException - openCache failed!", e);
		}
		return cache;
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