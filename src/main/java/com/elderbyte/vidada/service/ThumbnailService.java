package com.elderbyte.vidada.service;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MovieMediaItem;
import com.elderbyte.vidada.domain.settings.VidadaServerSettings;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 *
 *
 */
@Service
public class ThumbnailService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MediaService mediaService;
    private final Size maxThumbSize;
	private final ThumbImageExtractorService thumbImageCreator;
    private final MediaThumbCacheService mediaThumbCacheService;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new ThumbnailService
     * @param mediaService
     */
    @Inject
	public ThumbnailService(MediaService mediaService, MediaThumbCacheService mediaThumbCacheService, ThumbImageExtractorService thumbImageCreator) {
        this.mediaService= mediaService;
        this.mediaThumbCacheService = mediaThumbCacheService;
        this.thumbImageCreator = thumbImageCreator;

		maxThumbSize = VidadaServerSettings.instance().getMaxThumbResolution();
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


	public IMemoryImage getThumbImage(MediaItem media, Size size) {

		IMemoryImage thumb = null;

        size = enforceSizeLimit(size);

        try {
            thumb = fetchThumb(media, size);
        } catch (Exception e) {
            logger.error("Fetching thumbnail failed.", e);
        }

		return thumb;
	}


    public void renewThumbImage(MovieMediaItem media, float pos) {

        logger.info("Renewing thumbnail at position " + pos);

        try {
            // Remove old thumb from cache
            mediaThumbCacheService.removeImage(media);

            // Set the persisted thumb position to invalid
            // This will cause the creation of a new random thumb
            media.setPreferredThumbPosition(pos);
            media.setCurrentThumbPosition(MovieMediaItem.INVALID_POSITION);
            mediaService.update(media);
        } catch (Exception e) {
            logger.error("Could not renew thumb image", e);
        }
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Enforces the maximum size limit.
     * This will return the requested size as long as it is in the bounds of the maxThumbSize.
     * @param requestedSize
     * @return
     */
    private Size enforceSizeLimit(Size requestedSize){
        return new Size(
                Math.min(maxThumbSize.width, requestedSize.width),
                Math.min(maxThumbSize.height, requestedSize.height)
        );
    }


	/**
	 *  Fetches a thumb from the given media in the specified size.
	 * 	The thumb will be retrieved from a cached bigger version if possible.
	 *
	 * @param media
	 * @param size
	 * @return
	 * @throws Exception
	 */
	private IMemoryImage fetchThumb(MediaItem media, Size size) throws Exception {

		IMemoryImage loadedImage;

		if(size == null)
			size = maxThumbSize;

		loadedImage = mediaThumbCacheService.getImage(media, size);


		if(loadedImage == null) {

			// We need to fetch the thumb directly from the source.

			if(thumbImageCreator.canExtractThumb(media)){

                // Since fetching directly from source takes a very long time,
                // we fetch the largest possible thumb and derive  smaller sizes
                // from it by rescaling.

				IMemoryImage maxThumb = thumbImageCreator.extractThumb(media, maxThumbSize);

				if(maxThumb != null){
                    mediaThumbCacheService.storeImage(media, maxThumb);

					if(size.equals(maxThumbSize)){
						loadedImage = maxThumb;
					}else{
						loadedImage = maxThumb.rescale(size.width, size.height);
                        mediaThumbCacheService.storeImage(media, loadedImage);
					}
				}
			}
		}
		return loadedImage;
	}

}
