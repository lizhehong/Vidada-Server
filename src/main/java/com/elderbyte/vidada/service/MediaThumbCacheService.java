package com.elderbyte.vidada.service;

import archimedes.core.images.IMemoryImage;
import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.vidada.domain.images.cache.*;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.domain.media.Resolution;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.domain.security.ICredentialManager;
import com.elderbyte.vidada.VidadaSettings;
import com.elderbyte.vidada.service.images.ImageCacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


/**
 * Manages the thumbnail cache for all medias
 */
@Service
public class MediaThumbCacheService  {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String VidataCacheFolder = "vidada.db";
    public static final String VidataThumbsFolder = VidataCacheFolder + "/thumbs";

    /** Each media library has its own cache instance */
    private final Map<String, IImageCache> caches = new HashMap<>();

    private final IImageCache globalCache;
    private final Resolution maxThumbnailSize;
    private final ImageCacheFactory imageCacheFactory;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/


    /**
     * Creates a new GlobalImageCacheManager without a central file cache.
     * Instead, a central memory cache is used during the session.
     */
    @Autowired
    public MediaThumbCacheService(ICredentialManager credentialManager, ImageCacheFactory imageCacheFactory, VidadaSettings settings){

        this.imageCacheFactory = imageCacheFactory;

        maxThumbnailSize = settings.getMaxThumbResolution();

        globalCache = new MemoryImageCache();
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    public void removeImage(MediaItem media, String subId) {
        IImageCache imageCache = findImageCache(media);
        imageCache.removeImage(toThumbId(media, subId));
    }

    /**
     * Returns the cached thumbnail if available.
     *
     * @param media The media (used for id and library based image-caches)
     * @param subId A sub id of the thumb, i.e. useful for movies with positions
     * @param size The desired size
     * @return
     */
    public IMemoryImage getImage(MediaItem media, String subId, Resolution size) {

        IMemoryImage loadedImage = null;

        IImageCache imageCache = findImageCache(media);
        if(imageCache != null){

            String thumbId = toThumbId(media, subId);

            loadedImage = imageCache.getImageById(thumbId, size);

            if(loadedImage == null){
                // Bad luck, the image does not exist in the cache.

                // But we may already have cached a bigger version of the requested size.
                // If so, we rescale this bigger thumb to the required size.
                loadedImage = CacheUtils.getRescaledInstance(imageCache, thumbId, size);
                if(loadedImage != null) {
                    storeImage(media, subId, loadedImage);
                }
            }
        }
        return loadedImage;
    }

    /**
     * Stores the given thumbnail in this cache.
     *
     * @param media The media item
     * @param subId A sub id of the thumb, i.e. useful for movie medias with many thumbs
     * @param thumbnail
     */
    public void storeImage(MediaItem media, String subId, IMemoryImage thumbnail) {
        IImageCache imageCache = findImageCache(media);
        if(imageCache != null){
            String thumbId = toThumbId(media, subId);
            imageCache.storeImage(thumbId, thumbnail);
        }
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    private String toThumbId(MediaItem media, String subId){
        return media.getFilehash() + "_" + subId;
    }


    /**
     * Gets the image cache for the given media
     * @param media
     * @return
     */
    private IImageCache findImageCache(MediaItem media){
        IImageCache imageCache = null;

        MediaSource source = media.getSource();
        if(source != null)
        {
            MediaLibrary library = source.getParentLibrary();
            imageCache = getLibraryCache(library);
        }

        if(imageCache == null){
            imageCache = globalCache;
            logger.warn("No file based image cache available, falling back to memory cache!");
        }

        return imageCache;
    }

    /**
     * Gets the media library cache for the given library.
     * @param library
     * @return
     */
    private synchronized IImageCache getLibraryCache(MediaLibrary library){

        DirectoryLocation libraryRoot = library.getLibraryRoot();
        String libraryKey = libraryRoot.getPath().toLowerCase();

        if(!caches.containsKey(libraryKey)){
            caches.put(libraryKey, buildLibraryCache(libraryRoot));
        }

        return caches.get(libraryKey);
    }

    private IImageCache buildLibraryCache(DirectoryLocation libraryRoot){
        IImageCache libraryCache = buildLibraryLocalCache(libraryRoot);
        libraryCache = new SizeFilterCacheProxy(libraryCache, maxThumbnailSize);
        IImageCache cache = imageCacheFactory.leveledCache(globalCache, libraryCache);
        return cache;
    }

    /**
     * Builds a new cache for the given library
     * @param libraryRoot
     * @return
     */
    private IImageCache buildLibraryLocalCache(DirectoryLocation libraryRoot){

        if(libraryRoot != null && libraryRoot.exists()){
            try {
                DirectoryLocation libCache = DirectoryLocation.Factory.create(libraryRoot, VidataThumbsFolder);
                logger.info("Opening new library cache " + libraryRoot + "...");
                return imageCacheFactory.openCache(libCache);
            } catch (URISyntaxException e1) {
                logger.error("Failed to access library cache - path issue! " + libraryRoot, e1);
            }
        }
        logger.warn("Could not build image cache for library folder " + libraryRoot);
        return null;
    }


}
