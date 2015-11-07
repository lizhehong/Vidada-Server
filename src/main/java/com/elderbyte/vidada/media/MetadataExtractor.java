package com.elderbyte.vidada.media;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.ffmpeg.VideoInfo;
import com.elderbyte.vidada.media.*;
import com.elderbyte.vidada.media.libraries.MediaLibrary;
import com.elderbyte.vidada.media.source.MediaSource;
import com.elderbyte.vidada.video.IVideoAccessService;
import com.elderbyte.vidada.video.Video;
import com.elderbyte.vidada.images.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

    @Autowired
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

        MediaSource source = imageMedia.getSource();

        ResourceLocation imageResource = source.getResourceLocation();
        if(imageResource != null && imageResource.exists()){
            try{
                Resolution resolution = ImageUtil.getImageResolution(imageResource);

                if(resolution != null){
                    imageMedia.setResolution(resolution);
                    success = true;
                }else
                    logger.error("Could not fetch image resolution!");
            }catch(Exception e){
                logger.error("",e);
            }
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
