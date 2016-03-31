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
