package com.elderbyte.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;

class FFmpegInteropWindows extends FFmpegInterop
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String ffmpegCommand = "ffmpeg";


    public FFmpegInteropWindows(){
        try {
            File ffmpeg = ResourceUtil.extractResource("tools/ffmpeg.exe");
            if(ffmpeg != null && ffmpeg.exists()){
                ffmpegCommand = shieldPathArgument(ffmpeg);
            }
        } catch (IOException e) {
            logger.error("Failed to extract ffmeg!", e);
        }
    }

	@Override
	protected String shieldPathArgument(File pathArg){
		return pathArg.getAbsolutePath();
	}

	@Override
	protected String shieldPathArgument(URI pathArg){
		String path = pathArg.getPath();
		return path.substring(1, path.length());
	}


	@Override
	public String ffmpegCmd() {
		return ffmpegCommand;
	}

}
