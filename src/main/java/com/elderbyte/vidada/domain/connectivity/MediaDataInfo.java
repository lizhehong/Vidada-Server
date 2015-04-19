package com.elderbyte.vidada.domain.connectivity;



import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MovieMediaItem;
import com.elderbyte.vidada.domain.tags.Tag;

import java.util.ArrayList;
import java.util.List;


/**
 * Holds information about a single media data
 * such as Tags, Rating and other custom information
 *
 * @author IsNull
 *
 */
public class MediaDataInfo {

	public MediaDataInfo(){
		Tags = new ArrayList<String>();
	}


	public MediaDataInfo(MovieMediaItem movieMedia){
		this((MediaItem)movieMedia);
		ThumbPosition = movieMedia.getPreferredThumbPosition();
	}

	public MediaDataInfo(MediaItem media){
		File = media.getFilehash();
		Rating = media.getRating();
		Tags = new ArrayList<String>();

		for (Tag t : media.getTags()) {
			Tags.add(t.getName());
		}
	}


	/**
	 * Does this info object contain any valuable information?
	 * @return
	 */
	public boolean hasInfo() {
		return Rating != 0 || ThumbPosition != MovieMediaItem.INVALID_POSITION || !Tags.isEmpty();
	}


	// MediaData
	/**
	 * File Id (Hash)
	 */
	public String File;
	public int Rating;
	public List<String> Tags;

	// MoviePart
	public float ThumbPosition = MovieMediaItem.INVALID_POSITION;


}
