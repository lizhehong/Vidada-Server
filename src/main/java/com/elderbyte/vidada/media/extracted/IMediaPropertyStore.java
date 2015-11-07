package com.elderbyte.vidada.media.extracted;


import com.elderbyte.vidada.media.MediaItem;

/**
 * Defines a simple property repository API so store and retrieve named properties.
 *
 */
public interface IMediaPropertyStore {

	/**
	 * Gets the property from the store
	 * @param media The media item whose property shall be fetched
	 * @param key The name of the property
	 * @return The value or null if no property with the given key exists
	 */
	String getProperty(MediaItem media, String key);

	/**
	 * Sets the property and persists it
	 * @param media The media item whose property is set
	 * @param key The name of the property
	 * @param value The new value of the property
	 * @return
	 */
	void setProperty(MediaItem media, String key, String value);
}
