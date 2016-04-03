package com.elderbyte.vidada.thumbnails;

import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.NotSupportedException;
import com.elderbyte.vidada.images.IMemoryImage;
import com.elderbyte.vidada.images.IRawImageFactory;
import com.elderbyte.vidada.media.*;
import com.elderbyte.vidada.media.source.MediaSource;
import com.elderbyte.vidada.video.IVideoAccessService;
import com.elderbyte.vidada.video.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Thumbnail extractor for video and image files.
 */
@Service
public class ThumbImageExtractorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IRawImageFactory imageFactory;
    private final IVideoAccessService videoAccessService;


    @Autowired
    public ThumbImageExtractorService(IRawImageFactory imageFactory, IVideoAccessService videoAccessService){
        this.videoAccessService = videoAccessService;
        this.imageFactory = imageFactory;
    }


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

    /**
     * Returns true if a thumbnail can be extracted from the given media file.
     * @param media
     * @return
     */
	public boolean canExtractThumb(MediaItem media){
		if(media.getType() == MediaType.IMAGE)
			return true;
		if(media.getType() == MediaType.MOVIE){

            if(!((MovieMediaItem) media).canCreateThumbnail()){
                logger.warn("Extracting thumbnail is not possible for media " + media);
                return false;
            }
            if(!isVideoAccessServiceAvailable()){
                logger.warn("Extracting thumbnail is not possible since VideoAccessService reports not available on this system!");
                return false;
            }
            return true;
		}else{
            logger.warn("Unknown media type: " + media.getClass().getName());
			return false;
		}
	}

    /**
     * Extracts a thumbnail from the given media in the given resolution
     * @param media
     * @param size The desired resolution of the thumbnail.
     * @param position Relative frame position  [0.0 - 1.0]
     * @return
     */
	public IMemoryImage extractThumb(MediaItem media, Resolution size, float position){
		IMemoryImage image = null;

        logger.info("Extracting thumbnail for media " + media.getFilehash() + " ("+ media.getType() +"), size: " + size);

		if(media.getType() == MediaType.IMAGE){
			image = extractImageThumb((ImageMediaItem)media, size);
		}else if(media.getType() == MediaType.MOVIE){
			image = extractMovieThumb((MovieMediaItem)media, size, position);
		}else
			throw new NotSupportedException("Unknown media type: " + media.getClass().getName());

		return image;
	}



	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/


	/**
	 * Read the image file into a IMemoryImage in its native resolution
	 */
	private IMemoryImage readNativeImage(ImageMediaItem media) {
		IMemoryImage bufferedImage = null;

		MediaSource source = media.getSource();

		if(source != null){
			ResourceLocation filePath = source.getResourceLocation();
            try {
                if (filePath != null && filePath.exists()) {
                    logger.debug("Reading image...");
                    InputStream is = filePath.openInputStream();
                    bufferedImage = imageFactory.createImage(is);
                    is.close();
                }
            } catch (IOException e) {
                logger.warn("Failed to read image!", e);
            }
        }
		return bufferedImage;
	}

	private IMemoryImage extractImageThumb(ImageMediaItem media, Resolution size){
		IMemoryImage nativeImage = readNativeImage(media);
		if(nativeImage != null)
			nativeImage = nativeImage.rescale(size.getWidth(), size.getHeight());
		return nativeImage;
	}

	/**
	 * Create a thumb at the given position.
	 *
	 * @param size
	 * @param position
	 *            0.0 - 1.0
	 * @return
	 */
	private IMemoryImage extractMovieThumb(MovieMediaItem media, Resolution size, float position) {
        IMemoryImage frame = null;
		Video video = getVideo(media);
        if(video != null){
            frame = video.getFrame(position, size);
        }
		return frame;
	}

    private boolean isVideoAccessServiceAvailable(){
        return videoAccessService.isAvailable();
    }


    private Video getVideo(MovieMediaItem media){
		Video video = null;
		MediaSource source = media.getSource();
		if(source != null && source.isAvailable())
		{
			video = new Video(source.getResourceLocation(), videoAccessService);
		}
		return video;
	}

}
