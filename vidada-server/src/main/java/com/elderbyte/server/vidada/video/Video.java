package com.elderbyte.server.vidada.video;

import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.ffmpeg.VideoInfo;
import com.elderbyte.server.vidada.images.IMemoryImage;
import com.elderbyte.server.vidada.media.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Video-File
 * @author IsNull
 *
 */
public class Video {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final IVideoAccessService videoAccessService;


	private ResourceLocation videoResource;
	private VideoInfo videoInfo = null;


	public Video(ResourceLocation videoResource, IVideoAccessService videoAccessService){

        if(videoResource == null) throw new ArgumentNullException("videoResource");
        if(videoAccessService == null) throw new IllegalArgumentException("videoAccessService");

        this.videoAccessService = videoAccessService;
        this.videoResource = videoResource;
	}

	/**
	 * Returns the VideoInfo
	 * @return
	 */
	public VideoInfo getVideoInfo(){
		if(videoInfo == null){
            videoInfo = videoAccessService.extractVideoInfo(videoResource.getUri());
		}
		return videoInfo;
	}

	/**
	 * Gets the frame at the given position in its native resolution
	 *
	 * @param position 0.0 - 1.0 Relative position in the video
	 * @return
	 */
	public IMemoryImage getNativeFrame(float position){
		return getFrame(position, null);
	}

	/**
	 * Gets the frame at the given position in the requested resolution
	 * @param position
	 * @param thumbSize
	 * @return
	 */
	public IMemoryImage getFrame(float position, Resolution thumbSize){
        return videoAccessService.extractFrame(videoResource.getUri(), position, thumbSize);
	}

}
