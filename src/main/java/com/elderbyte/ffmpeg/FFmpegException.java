package com.elderbyte.ffmpeg;


/**
 * Indicates a problem related in the native ffmpeg system.
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class FFmpegException extends Exception{

    public FFmpegException(String message){
        super(message);
    }

	public FFmpegException(String message, Throwable cause){
		super(message, cause);
	}
}
