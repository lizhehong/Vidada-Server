package com.elderbyte.ffmpeg;


import archimedes.core.util.FileSupport;

/**
 * Indicates a problem related in the native ffmpeg system
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class FFmpegException extends Exception{

	public FFmpegException(String message, String commandline){
		super(message + FileSupport.NEWLINE + FileSupport.NEWLINE + "shell:" + FileSupport.NEWLINE + commandline);
	}
}
