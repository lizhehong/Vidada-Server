package com.elderbyte.vidada.cache;

import com.elderbyte.vidada.images.IMemoryImage;
import com.elderbyte.vidada.media.Resolution;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Implements a basic memory image cache for super fast
 * image access.
 *
 * The cache uses a LRU Cache to stay inside a max boundary
 * of memory consumption. Yet, the values are not dependent on
 * any system and VM configuration such as the available memory.
 *
 *
 *
 * @author IsNull
 *
 */
public class MemoryImageCache extends ImageCacheProxyBase {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MemoryImageCache.class.getName());

	private final Map<Resolution, Cache<String, IMemoryImage>  > imageCache;

	/**
	 * Average scaled image size in MB
	 */
	private final static float AVERAGE_SCALED_IMAGE_SIZE = 0.7f;


	/**
	 * Maximum amount in MB the scaled image cache is allowed to take
	 *
	 * This cache is much more important in the usage, as
	 * generally the native image is only required to create
	 * the scaled thumbs and afterwards is no longer loaded.
	 * Therefore this cache has a higher overall usage rate.
	 *
	 * Each resolution has its own cache, so you need
	 * to divide the actual value by the number of caches you expect
	 */
	private static final float MAX_OVERALL_MEMORY_SCALED_CACHE = 2000f; //2GB


	/**
	 * Max numbers of cached resolution size folders
	 */
	private static int MAX_SCALED_CACHE_SIZEFOLDERS = 5;

	/**
	 * Max size of a single resolution cache
	 */
	public static final float MAX_MEMORY_SCALED_CACHE = MAX_OVERALL_MEMORY_SCALED_CACHE / MAX_SCALED_CACHE_SIZEFOLDERS;


    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a standalone memory image cache without an underlying cache.
     */
    public MemoryImageCache(){
        this(null);
    }

    /**
	 * Wraps this memory cache around the given IImageCache,
	 * that means that images only once are requested from the
	 * underlining cache.
	 * (Or in case the memory cache exceeds its size limit)
	 *
	 * @param unbufferedImageCache A slow cache which gets a memory cache wrapper
	 */
	public MemoryImageCache(IImageCache unbufferedImageCache){
        super(unbufferedImageCache);
		// Setup image cache
		this.imageCache = Collections.synchronizedMap(new HashMap<>());
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


	@Override
	public IMemoryImage getImageById(String id, Resolution size) {
		IMemoryImage image = null;

		if(existsInMemoryCache(id, size))
		{
			image = imageCache.get(size).getIfPresent(id);
		} else {
			image = super.getImageById(id, size);
            if(image != null) {
                storeImageInMemoryCache(id, image, size);
            }
		}
		return image;
	}

	@Override
	public Set<Resolution> getCachedDimensions(String id) {
        // TODO Memory cached dimensions?
		return super.getCachedDimensions(id);
	}

	@Override
	public boolean exists(String id, Resolution size) {
		return existsInMemoryCache(id, size) || super.exists(id, size);
	}

	@Override
	public void storeImage(String id, IMemoryImage image) {
        // TODO storing an existing image - probably remove old first
        super.storeImage(id, image);
		storeImageInMemoryCache(id, image);
	}

	@Override
	public void removeImage(String id) {

        super.removeImage(id);

		for (Resolution d : new ArrayList<>(imageCache.keySet())) {
            Cache<String, IMemoryImage> idImageMap = imageCache.get(d);

            idImageMap.invalidate(id);
			if(idImageMap.size() > 0) {
                imageCache.remove(d);
            }
		}
	}

    /***************************************************************************
     *                                                                         *
     * Protected methods                                                       *
     *                                                                         *
     **************************************************************************/


	protected boolean existsInMemoryCache(String id, Resolution size){
        Cache<String, IMemoryImage> cache = imageCache.get(size);
		if(cache != null){
			return cache.getIfPresent(id) != null;
		}
		return false;
	}


	protected void storeImageInMemoryCache(String id, IMemoryImage image) {
        if(image == null) throw new IllegalArgumentException("image must not be NULL");
        Resolution d = new Resolution(image.getWidth(), image.getHeight());
        storeImageInMemoryCache(id, image, d);
    }


	protected void storeImageInMemoryCache(String id, IMemoryImage image, Resolution imageSize) {
        if (image == null) throw new IllegalArgumentException("image must not be NULL");

        if (!imageCache.containsKey(imageSize)) {
            int maxScaledSize = (int) (MAX_MEMORY_SCALED_CACHE / AVERAGE_SCALED_IMAGE_SIZE);
            imageCache.put(imageSize, buildImageCache(maxScaledSize));

            logger.info("Created scaled image cache. maxScaledSize: " + maxScaledSize);
        }
        imageCache.get(imageSize).put(id, image);
    }


    private Cache<String, IMemoryImage> buildImageCache(int maxSize){
        return CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .softValues()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    }



}
