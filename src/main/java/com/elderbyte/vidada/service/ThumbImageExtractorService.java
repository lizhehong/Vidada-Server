package com.elderbyte.vidada.service;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.images.IRawImageFactory;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.domain.media.ImageMediaItem;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaType;
import com.elderbyte.vidada.domain.media.MovieMediaItem;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.domain.video.IVideoAccessService;
import com.elderbyte.vidada.domain.video.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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


    @Inject
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
			return ((MovieMediaItem) media).canCreateThumbnail() && isGenericEncoderPresent();
		}else{
            logger.warn("ThumbImageExtractor::canExtractThumb: Unknown media type: " + media.getClass().getName());
			return false;
		}
	}

    /**
     * Extracts a thumbnail from the given media in teh given resolution
     * @param media
     * @param size The desired resolution of the thumbnail.
     * @return
     */
	public IMemoryImage extractThumb(MediaItem media, Size size){
		IMemoryImage image = null;

        logger.info("Extracting thumbnail for media " + media.getFilehash() + " ("+ media.getType() +"), size: " + size);

		if(media.getType() == MediaType.IMAGE){
			image = extractImageThumb((ImageMediaItem)media, size);
		}else if(media.getType() == MediaType.MOVIE){
			image = extractMovieThumb((MovieMediaItem)media, size);
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
			if (filePath != null && filePath.exists()) {
                logger.debug("Reading image...");
				InputStream is = null;
				try {
					is = filePath.openInputStream();
					bufferedImage = imageFactory.createImage(is);
				} catch (Exception e) {
                    logger.error("Can not read image" + filePath.toString());
				}finally{
					if(is != null){
						try {
							is.close();
						} catch (IOException e) {
                            logger.error("", e);
						}
					}
				}
			}
		}
		return bufferedImage;
	}

	private IMemoryImage extractImageThumb(ImageMediaItem media, Size size){
		IMemoryImage nativeImage = readNativeImage(media);
		if(nativeImage != null)
			nativeImage = nativeImage.rescale(size.width, size.height);
		return nativeImage;
	}

	private IMemoryImage extractMovieThumb(MovieMediaItem media, Size size){
		return extractMovieThumb(media, size, media.getThumbPos());
	}

	/**
	 * Create a thumb at the given position.
	 *
	 * @param size
	 * @param position
	 *            0.0 - 1.0
	 * @return
	 */
	private IMemoryImage extractMovieThumb(MovieMediaItem media, Size size, float position) {

		Video video = getVideo(media);

		IMemoryImage frame = video.getFrame(position, size);
		if (frame != null) {
			media.setCurrentThumbPosition(position);
			media.onThumbCreationSuccess();
		} else {
			// the thumb could not be generated
			media.onThumbCreationFailed();
		}

		return frame;
	}

    private boolean isGenericEncoderPresent(){
        return videoAccessService.isAvailable();
    }


    private Video getVideo(MovieMediaItem media){
		Video video = null;
		MediaSource source = media.getSource();
		if(source != null && source.isAvailable())
		{
			ResourceLocation path = source.getResourceLocation();
			video = new Video(path, videoAccessService);
		}
		return video;
	}

}
