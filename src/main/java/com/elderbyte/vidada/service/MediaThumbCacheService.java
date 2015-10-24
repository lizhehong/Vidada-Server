package com.elderbyte.vidada.service;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.vidada.domain.images.cache.*;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.domain.security.ICredentialManager;
import com.elderbyte.vidada.VidadaSettings;
import com.elderbyte.vidada.service.images.ImageCacheFactory;
import org.hibernate.annotations.Synchronize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


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

    /** Each media library has its own cache instance*/
    private final Map<String, IImageCache> caches = new HashMap<>();

    private final IImageCache globalCache;
    private final Size maxThumbnailSize;
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
    @Inject
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

    public void removeImage(MediaItem media) {
        IImageCache imageCache = getImageCache(media);
        imageCache.removeImage(media.getFilehash());
    }

    public IMemoryImage getImage(MediaItem media, Size size) {

        IMemoryImage loadedImage = null;

        IImageCache imageCache = getImageCache(media);
        if(imageCache != null){
            loadedImage = imageCache.getImageById(media.getFilehash(), size);

            if(loadedImage == null){
                // Bad luck, the image does not exist in the cache.

                // But we may already have cached a bigger version of the requested size.
                // If so, we rescale this bigger thumb to the required size.
                loadedImage = CacheUtils.getRescaledInstance(imageCache, media.getFilehash(), size);
                if(loadedImage != null) {
                    storeImage(media, loadedImage);
                }
            }
        }
        return loadedImage;
    }

    public void storeImage(MediaItem media, IMemoryImage thumbnail) {
        IImageCache imageCache = getImageCache(media);
        imageCache.storeImage(media.getFilehash(), thumbnail);
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    /**
     * Gets the image cache for the given media
     * @param media
     * @return
     */
    public IImageCache getImageCache(MediaItem media){
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
