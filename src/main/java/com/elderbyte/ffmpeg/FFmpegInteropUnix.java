package com.elderbyte.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.net.URI;


class FFmpegInteropUnix extends FFmpegInterop
{
	private static File encoder;

	static {
        try {
            encoder = ResourceUtil.extractResource("tools/ffmpeg");
            encoder.setExecutable(true);
        } catch (IOException e) {
            logger.error("Failed to extract ffmeg!", e);
        }
    }


	@Override
	public String getFFmpegCMD() {
		return shieldPathArgument(encoder);	//return "ffmpeg";
	}

	@Override
	protected String shieldPathArgument(File pathArg){
		return pathArg.getAbsolutePath();
	}

	@Override
	protected String shieldPathArgument(URI pathArg){
		return pathArg.getPath();
	}

	boolean isAvaiable;
	boolean isAvaiableDirty = true;

	@Override
	public boolean isAvaiable() {
		if(isAvaiableDirty){
			isAvaiable = encoder.exists();
			isAvaiableDirty = false;
		}

		return isAvaiable;
	}

}
