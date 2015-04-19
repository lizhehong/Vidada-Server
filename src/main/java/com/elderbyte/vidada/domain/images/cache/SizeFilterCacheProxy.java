package com.elderbyte.vidada.domain.images.cache;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;

/**
 * This is a proxy which controls storing of a image depending on its size.
 */
public class SizeFilterCacheProxy extends ImageCacheProxyBase {

    private final Size allowedSize;

    /**
     * Creates a new ImageCacheProxyBase
     *
     * @param original The original cache to wrap
     */
    public SizeFilterCacheProxy(IImageCache original, Size allowedSize) {
        super(original);
        this.allowedSize = allowedSize;
    }


    @Override
    public void storeImage(String id, IMemoryImage image) {
        if(isAllowed(image)) {
            super.storeImage(id, image);
        }
    }

    /**
     *
     * @param image
     * @return
     */
    private boolean isAllowed(IMemoryImage image){
        return (image.getWidth() == allowedSize.width && image.getHeight() == allowedSize.height);
    }
}
