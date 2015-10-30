package com.elderbyte.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.net.URI;

class FFmpegInteropWindows extends FFmpegInterop
{
	static {
        try {
            encoder = ResourceUtil.extractResource("tools/ffmpeg.exe");
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
	public String getFFmpegCMD() {
		return shieldPathArgument(encoder);
	}

	@Override
	public boolean isAvaiable() {
		return encoder.exists();
	}
}
