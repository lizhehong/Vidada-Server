package com.elderbyte.server.vidada.media;


/**
 * Represents the media type of a media item
 */
public enum MediaType {

	UNKNOWN("Unknown"), MOVIE("Movie"), IMAGE("Image");

	private final String displayName;

	MediaType(String displayName){
		this.displayName = displayName;
	}

	@Override
	public String toString(){
		return displayName;
	}
}
