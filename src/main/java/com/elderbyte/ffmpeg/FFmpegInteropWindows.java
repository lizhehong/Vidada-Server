package com.elderbyte.ffmpeg;


import com.elderbyte.ffmpeg.FFmpegInterop;

import java.io.File;
import java.net.URI;

class FFmpegInteropWindows extends FFmpegInterop
{
	static {
		encoder = extractFFMpeg("tools/ffmpeg.exe");
	}


	@Override
	public File getFFmpegBinaryFile() {
		return encoder;
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
		return shieldPathArgument(getFFmpegBinaryFile());
	}

	@Override
	public boolean isAvaiable() {
		return getFFmpegBinaryFile().exists();
	}
}
