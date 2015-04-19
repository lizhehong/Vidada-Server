package com.elderbyte.vidada.service;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.vidada.domain.images.cache.*;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.domain.security.ICredentialManager;
import com.elderbyte.vidada.domain.settings.VidadaServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
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

    private final IImageCache globalCache;
    private final Map<MediaLibrary, IImageCache> combinedCachesMap = new HashMap<>();
    private final Size maxThumbnailSize = VidadaServerSettings.instance().getMaxThumbResolution();
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
    public MediaThumbCacheService(ICredentialManager credentialManager, ImageCacheFactory imageCacheFactory){

        this.imageCacheFactory = imageCacheFactory;

        if(VidadaServerSettings.instance().getCurrentDBConfig().isUseLocalCache()){

            File cacheLocation = VidadaServerSettings.instance().getAbsoluteCachePath();
            IImageCache cache = imageCacheFactory.openEncryptedCache(cacheLocation, credentialManager);
            this.globalCache = new MemoryImageCache(cache);
        }else{
            globalCache = new MemoryImageCache();
        }
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

        IImageCache imageCache = getImageCache(media);
        IMemoryImage loadedImage = imageCache.getImageById(media.getFilehash(), size);

        if(loadedImage == null){
            // Bad luck, the image does not exist in the cache.

            // But we may already have cached a bigger version of the requested size.
            // If so, we rescale this bigger thumb to the required size.
            loadedImage = CacheUtils.getRescaledInstance(imageCache, media.getFilehash(), size);
            if(loadedImage != null) {
                storeImage(media, loadedImage);
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
        IImageCache imageCache;

        MediaSource source = media.getSource();
        if(source != null)
        {
            MediaLibrary library = source.getParentLibrary();
            imageCache = getLibraryCache(library);

        } else {
            imageCache = globalCache;
        }

        return imageCache;
    }

    @Cacheable("libraryImageCaches")
    private IImageCache getLibraryCache(MediaLibrary library){
        IImageCache libraryCache = buildLibraryCache(library);
        libraryCache = new SizeFilterCacheProxy(libraryCache, maxThumbnailSize);
        return imageCacheFactory.leveledCache(globalCache, libraryCache);
    }

    private IImageCache buildLibraryCache(MediaLibrary library){

        IImageCache cache = null;
        DirectoryLocation libraryRoot = library.getLibraryRoot();
        if(libraryRoot != null && libraryRoot.exists()){
            try {
                DirectoryLocation libCache = DirectoryLocation.Factory.create(libraryRoot, VidataThumbsFolder);
                logger.info("Opening new library cache...");
                cache = imageCacheFactory.openCache(libCache);
            } catch (URISyntaxException e1) {
                logger.error("Failed to access library cache - path issue!", e1);
            }
        }
        return cache;
    }


}
