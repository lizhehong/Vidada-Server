package com.elderbyte.vidada.service.media;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.geometry.Size;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.swing.images.ImageInfo;
import archimedes.core.swing.images.SimpleImageInfo;
import com.elderbyte.vidada.domain.media.ImageMediaItem;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.domain.media.MovieMediaItem;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.domain.video.IVideoAccessService;
import com.elderbyte.vidada.domain.video.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vidada.ffmpeg.VideoInfo;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts metadata from the raw media file and stores it in the media object.
 */
@Service
public class MetadataExtractor {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IVideoAccessService videoAccessService;

    @Inject
    public MetadataExtractor(IVideoAccessService videoAccessService){
        this.videoAccessService = videoAccessService;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Extracts metadata from the raw file and stores it in the media object.
     * @param media
     * @return Returns true if metadata was extracted.
     */
    public boolean extractMetadata(MediaItem media){
        switch (media.getType()){
            case IMAGE:
                return extractImageInfo( (ImageMediaItem)media );

            case MOVIE:
                return extractMovieInfo( (MovieMediaItem)media );

            default:
                throw new NotSupportedException("Not supported media type: " + media.getType());
        }
	}



    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


	private boolean extractMovieInfo(MovieMediaItem movieMedia){
		boolean success = false;
		Video myVideo = getVideo(movieMedia);
		if(myVideo != null){
			VideoInfo info = myVideo.getVideoInfo();
			if(info != null){
				movieMedia.setResolution(info.NativeResolution);
				movieMedia.setBitrate(info.BitRate);
                movieMedia.setDuration(info.Duration);
				success = true;
			}
		}
		return success;
	}


	private boolean extractImageInfo(ImageMediaItem imageMedia){
		boolean success = false;

		try {
			MediaSource source = imageMedia.getSource();

			ResourceLocation imagePath = source.getResourceLocation();
			if(imagePath != null && imagePath.exists()){

				InputStream is = null;
				try{
					is = imagePath.openInputStream();
					ImageInfo info = SimpleImageInfo.parseInfo(is);
					if(info != null && info.isValid()){
						Size resolution = new Size(info.getWidth(), info.getHeight());
						imageMedia.setResolution(resolution);
						success = true;
					}else
                        logger.error("resolveResolution(): ImageInfo is NOT VALID! " + info);
				}catch(Exception e){
                    logger.error("",e);
				}finally{
					if(is != null){
						is.close();
					}
				}
			}
		} catch (IOException e) {
            logger.error("",e);
		}

		return success;
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

	private MediaLibrary getLibrary(MediaItem media){
		MediaLibrary library = null;
		MediaSource source = media.getSource();
		if(source != null && source.isAvailable())
		{
			library = source.getParentLibrary();
		}
		return library;
	}



}
