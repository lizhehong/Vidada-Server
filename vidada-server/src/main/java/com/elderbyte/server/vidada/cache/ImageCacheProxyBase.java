package com.elderbyte.server.vidada.cache;

import archimedes.core.images.IMemoryImage;
import com.elderbyte.server.vidada.media.Resolution;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for a image cache proxy.
 * This implementation redirects all calls to this instance down to the "original".
 *
 * Subclasses may override methods of the proxy to transparently inject functionality.
 * @author IsNull
 *
 */
public abstract class ImageCacheProxyBase implements IImageCache {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

	transient private final IImageCache original;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new ImageCacheProxyBase
     * @param original The original cache to wrap
     */
	public ImageCacheProxyBase(IImageCache original)
    {
		this.original = original;
	}


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	@Override
	public IMemoryImage getImageById(String id, Resolution size) {
		return original != null ? original.getImageById(id, size) : null;
	}

	@Override
	public Set<Resolution> getCachedDimensions(String id) {
		return original != null ? original.getCachedDimensions(id) : new HashSet<>();
	}

	@Override
	public boolean exists(String id, Resolution size) {
		return original != null && original.exists(id, size);
	}

	@Override
	public void storeImage(String id, IMemoryImage image) {
		if(original != null)
			original.storeImage(id, image);
	}

	@Override
	public void removeImage(String id) {
		if(original != null)
			original.removeImage(id);
	}

    /***************************************************************************
     *                                                                         *
     * Protected methods                                                       *
     *                                                                         *
     **************************************************************************/

    /**
     * Returns the original cache
     */
    protected final IImageCache getOriginalCache() {
        return original;
    }

}
