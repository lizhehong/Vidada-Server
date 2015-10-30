package com.elderbyte.ffmpeg;

import java.io.File;
import java.net.URI;

/**
 * frame=    0 fps=0.0 q=0.0 size=N/A time=00:00:00.00 bitrate=N/A
 *
 *??
 * @author IsNull
 *
 */
class FFmpegInteropUnix extends FFmpegInterop
{
	private static File encoder;

	static {
		encoder = extractFFMpeg("tools/ffmpeg");
	}

	@Override
	public File getFFmpegBinaryFile() {
		return encoder;
	}

	@Override
	public String getFFmpegCMD() {
		return shieldPathArgument(getFFmpegBinaryFile()); 		//return "ffmpeg";
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
			isAvaiable = getFFmpegBinaryFile().exists();
			isAvaiableDirty = false;
		}

		return isAvaiable;
	}

}
