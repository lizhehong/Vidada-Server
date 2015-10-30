package com.elderbyte.ffmpeg;


import archimedes.core.geometry.Size;

public class VideoInfo {

	/**
	 * Gets the duration of this video in Seconds
	 */
	public final int Duration;

	/**
	 * Gets the bitrate in kb/s
	 */
	public final int BitRate;

	/**
	 * Gets the native video resolution
	 */
	public final Size NativeResolution;


	public boolean hasAllInfos(){
		return Duration != 0 && BitRate != 0 && NativeResolution != null;
	}


	/**
	 *
	 * @param duration duration in seconds
	 * @param bitrate bitrate in Kb
	 * @param resolution resolution in pixels
	 */
	public VideoInfo(int duration, int bitrate, Size resolution){
		this.Duration = duration;
		this.BitRate = bitrate;
		this.NativeResolution = resolution;
	}
}
