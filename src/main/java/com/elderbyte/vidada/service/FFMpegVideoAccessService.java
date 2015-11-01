package com.elderbyte.vidada.service;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.images.IRawImageFactory;

import com.elderbyte.ffmpeg.FFmpegException;
import com.elderbyte.ffmpeg.FFmpegInterop;
import com.elderbyte.ffmpeg.VideoInfo;
import com.elderbyte.vidada.domain.video.IVideoAccessService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@Service
public class FFMpegVideoAccessService implements IVideoAccessService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    private final IRawImageFactory imageFactory;
	private final static FFmpegInterop ffmpeg = FFmpegInterop.instance();
	private final Map<String, VideoInfo> videoInfoCache = new HashMap<>();


    @Autowired
    public FFMpegVideoAccessService(IRawImageFactory imageFactory){
        this.imageFactory = imageFactory;

        try {
            logger.info("Initialized ffmpeg wrapper @ version " + ffmpeg.ffmpegVersion());
        } catch (FFmpegException e) {
            logger.warn("Failed to initialize ffmpeg!");
        }
    }



    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	@Override
	public VideoInfo extractVideoInfo(URI pathToVideFile) {
		String pathstr = pathToVideFile.toString();
		if(!videoInfoCache.containsKey(pathstr))
		{
            VideoInfo info = null;
            try {
                info = ffmpeg.getVideoInfo(pathToVideFile);
                if(info.hasAllInfos()) {
                    videoInfoCache.put(pathstr, info);
                }
            } catch (FFmpegException e) {
               logger.error("Failed to extract video info!", e);
            }
		}
        if(videoInfoCache.containsKey(pathstr)){
            return videoInfoCache.get(pathstr);
        }
		return null;
	}


	@Override
	public IMemoryImage extractNativeFrame(URI pathToVideFile, int second) {
		IMemoryImage frame = null;

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			Size resoulution = info.NativeResolution;
			if(resoulution != null)
				frame = extractFrame(pathToVideFile, second, resoulution);
		}
		return frame;
	}

	/**
	 * extractNativeFrame
	 */
	@Override
	public IMemoryImage extractNativeFrame(URI pathToVideFile, float position) {

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			if(info.NativeResolution != null){
				return extractFrame(pathToVideFile, position, info.NativeResolution);

			}else{
                logger.warn("extractNativeFrame: video info NativeResolution is unavaiable.");
			}
		}else{
            logger.warn("extractNativeFrame: video info could not be extracted!");
		}

		return null;
	}

	@Override
	public IMemoryImage extractFrame(URI pathToVideFile, float position, Size frameSize) {
		position = Math.min(1f, Math.abs(position)); // ensure position is in valid range

		IMemoryImage frame = null;

		VideoInfo info = extractVideoInfo(pathToVideFile);
		if(info != null)
		{
			if(info.Duration != 0){
				int second =(int)((float)info.Duration * position);
				frame = extractFrame(pathToVideFile, second, frameSize);

			}else{
                logger.warn("extractFrame: video Duration info is unavailable");
			}
		}else{
            logger.warn("extractFrame: video info could not be extracted!");
		}

		return frame;
	}

    @Override
    public boolean isAvailable() {
        return ffmpeg.isAvailable();
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

	/**
	 * Returns a frame of this video at the given position and with the given size
	 *
	 * @param second
	 * @param size
	 * @return returns a bufferedimage representing the frame
	 */
	private IMemoryImage extractFrame(URI pathToVideFile, int second, Size size){
		IMemoryImage frame = null;

		File pathToImage = null;

		try {
			pathToImage = File.createTempFile("thumb", ".png");
			try {
				ffmpeg.extractFrame(pathToVideFile, pathToImage, second, size);
				//load the file...
				frame = imageFactory.createImage(pathToImage);

			} catch (FFmpegException e) {
				logger.error("", e);
			}
		} catch (IOException e1) {
            logger.error("", e1);
		}finally{
			// remove the temporary image
			FileUtils.deleteQuietly(pathToImage);
		}

		return frame;
	}



}
