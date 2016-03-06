package com.elderbyte.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;


class FFmpegInteropUnix extends FFmpegInterop
{
    private String ffmpegCommand = "ffmpeg";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    public FFmpegInteropUnix(){
        try {
            File ffmpeg = ResourceUtil.extractResource("tools/ffmpeg");
            if(ffmpeg != null && ffmpeg.exists()){
                ffmpeg.setExecutable(true);
                ffmpegCommand = shieldPathArgument(ffmpeg);
            }
        } catch (IOException e) {
            logger.error("Failed to extract ffmeg! " + e.getMessage());
        }
    }

	@Override
	public String ffmpegCmd() {
		return ffmpegCommand;
	}

	@Override
	protected String shieldPathArgument(File pathArg){
		return pathArg.getAbsolutePath();
	}

	@Override
	protected String shieldPathArgument(URI pathArg){
		return pathArg.getPath();
	}


}
