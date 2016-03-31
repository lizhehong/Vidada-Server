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
