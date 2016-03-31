package com.elderbyte.server.vidada.thumbnails;

import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.server.vidada.VidadaSettings;
import com.elderbyte.server.vidada.images.IMemoryImage;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;


/**
 * Manages all Thumbnails and their creation
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
	private ThumbImageExtractorService thumbImageCreator;
    @Autowired
    private MediaThumbCacheService mediaThumbCacheService;

    private ThumbnailFailCounter failCounter = new ThumbnailFailCounter(3);
    private final ForkJoinPool mainPool = new ForkJoinPool(3);

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
    public CompletableFuture<IMemoryImage> getThumbnailAsync(MediaItem media, float position) {
        return getThumbnailAsync(media, null, position);
    }



    /**
     * Returns a thumbnail for the given media in the desired size.
     *
     * @param media The media from which the thumbnail should be fetched
     * @param resolution The desired thumbnail resolution. Note that there is a hard limit for this to avoid abuse.
     * @param position Relative frame position, ignored for images.
     * @return
     */
	public CompletableFuture<IMemoryImage> getThumbnailAsync(final MediaItem media, Resolution resolution, float position) {

        if(media == null) throw new ArgumentNullException("media");

        String subId = position + "";

        // Ensure we stay in reasonable dimensions, if null it will be max possible
        final Resolution actualResolution = enforceSizeLimit(resolution);

        IMemoryImage loadedImage = mediaThumbCacheService.getImage(media, subId, actualResolution);

        if(loadedImage != null) {
            // Image already available - return immediately
            return CompletableFuture.completedFuture(loadedImage);
        }else{

            if(!failCounter.hasFailedTooOften(media)){
                logger.info("No cached thumb available for media " + media.getTitle() + " - enqueuing async thumb creation...");
                // We need to fetch the thumb directly from the source.
                return CompletableFuture.supplyAsync(() -> fetchThumbSync(media, actualResolution, position), mainPool);
            }else{
                logger.warn("Thumbnail creation failed too often for media " + media.getTitle());
                return CompletableFuture.completedFuture(null);
            }
        }
    }

    public int getQueuedTaskCount(){
        return mainPool.getQueuedSubmissionCount();
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
     * @param position
     * @return
     */
    private IMemoryImage fetchThumbSync(MediaItem media, Resolution size, float position){

        if(media == null) throw new ArgumentNullException("media");
        if(size == null) throw new ArgumentNullException("size");

        String subId = position + "";

        IMemoryImage thumb;
        thumb = mediaThumbCacheService.getImage(media, subId, size);

        if(thumb == null) {
            // Only fetch thumb from source if not already in cache
            if (thumbImageCreator.canExtractThumb(media)) {

                // Since fetching directly from source takes a very long time,
                // we fetch the largest possible thumb and derive  smaller sizes
                // from it by rescaling.

                IMemoryImage maxThumb = thumbImageCreator.extractThumb(media, maxThumbSize, position);

                if (maxThumb != null) {
                    // Thumb was successful extracted

                    failCounter.onThumbCreationSuccess(media);
                    mediaThumbCacheService.storeImage(media, subId, maxThumb);

                    if (size.equals(maxThumbSize)) {
                        thumb = maxThumb;
                    } else {
                        thumb = maxThumb.rescale(size.getWidth(), size.getHeight());
                        mediaThumbCacheService.storeImage(media, subId, thumb);
                    }
                }else{
                    failCounter.onThumbCreationFailed(media);
                }
            } else {
                logger.warn("Thumbnail extraction is not possible!");
            }
        }
        return thumb;
    }








}
