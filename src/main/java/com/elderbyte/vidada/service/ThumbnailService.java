package com.elderbyte.vidada.service;

import archimedes.core.images.IMemoryImage;
import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MovieMediaItem;
import com.elderbyte.vidada.VidadaSettings;
import com.elderbyte.vidada.domain.media.Resolution;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


/**
 * Manages all Thumbnails
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

    @Autowired
    private MediaService mediaService;
    @Autowired
	private ThumbImageExtractorService thumbImageCreator;
    @Autowired
    private MediaThumbCacheService mediaThumbCacheService;
    @Autowired
    private BackgroundTaskService backgroundTaskService;

    private final Resolution maxThumbSize;


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new ThumbnailService
     * @param settings
     */
    @Autowired
	public ThumbnailService(VidadaSettings settings) {

		maxThumbSize = settings.getMaxThumbResolution();
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    /**
     * Returns a thumbnail for the given media with max available size.
     *
     * @param media The media from which the thumbnail should be fetched
     *
     * @return
     */
    public CompletableFuture<IMemoryImage> getThumbnailAsync(MediaItem media) {
        return getThumbnailAsync(media, null);
    }



    /**
     * Returns a thumbnail for the given media in the desired size.
     *
     * @param media The media from which the thumbnail should be fetched
     * @param resolution The desired thumbnail resolution. Note that there is a hard limit for this to avoid abuse.
     * @return
     */
	public CompletableFuture<IMemoryImage> getThumbnailAsync(final MediaItem media, Resolution resolution) {

        if(media == null) throw new ArgumentNullException("media");

        // Ensure we stay in reasonable dimensions, if null it will be max possible
        final Resolution actualResolution = enforceSizeLimit(resolution);


        IMemoryImage loadedImage = mediaThumbCacheService.getImage(media, actualResolution);

        if(loadedImage != null) {
            // Image already available - return immediately
            return CompletableFuture.completedFuture(loadedImage);
        }else{

            logger.info("No cached thumb available for media " + media.getFilehash() + " enqueuing async thumb creation...");
            // We need to fetch the thumb directly from the source.
            return backgroundTaskService.submitTask(() -> fetchThumbSync(media, actualResolution));
        }
    }


    /**
     * Creates a new thumbnail for the given media at the given position.
     * @param media
     * @param pos A relative position in the movie range [0.0-1.0]
     */
    public void renewThumbImage(MovieMediaItem media, float pos) {

        if(media == null) throw new ArgumentNullException("media");


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
     * Enforces the size limit.
     * This will return the requested size as long as it is in the bounds of the maxThumbSize.
     * @param requestedSize
     * @return
     */
    private Resolution enforceSizeLimit(Resolution requestedSize){

        if(requestedSize == null){
            return maxThumbSize;
        }

        return new Resolution(
                Math.min(maxThumbSize.getWidth(), requestedSize.getWidth()),
                Math.min(maxThumbSize.getHeight(), requestedSize.getHeight())
        );
    }


    /**
     * Fetches the media thumbnail synchronously - may take quite some time!
     * @param media
     * @param size
     * @return
     */
    private IMemoryImage fetchThumbSync(MediaItem media, Resolution size){

        if(media == null) throw new ArgumentNullException("media");
        if(size == null) throw new ArgumentNullException("size");


        IMemoryImage thumb;
        thumb = mediaThumbCacheService.getImage(media, size);

        if(thumb == null) {
            // Only fetch thumb from source if not already in cache
            if (thumbImageCreator.canExtractThumb(media)) {

                // Since fetching directly from source takes a very long time,
                // we fetch the largest possible thumb and derive  smaller sizes
                // from it by rescaling.

                IMemoryImage maxThumb = thumbImageCreator.extractThumb(media, maxThumbSize);

                if (maxThumb != null) {
                    mediaThumbCacheService.storeImage(media, maxThumb);

                    if (size.equals(maxThumbSize)) {
                        thumb = maxThumb;
                    } else {
                        thumb = maxThumb.rescale(size.getWidth(), size.getHeight());
                        mediaThumbCacheService.storeImage(media, thumb);
                    }
                }
            } else {
                logger.warn("Thumbnail extraction is not possible!");
            }
        }
        return thumb;
    }



}
