package com.elderbyte.vidada.media.extracted;

import java.util.HashMap;
import java.util.Map;

/**
 * Package protected helper class used to hold all properties for a single media item.
 * Used by {@link JsonMediaPropertyStore} and serialized to a json file.
 */
class PropertyContainer {

	public Map<String, String> properties = new HashMap<String, String>();

	public String getProperty(String key){
		return properties.get(key);
	}

	public void setProperty(String key, String value){
		properties.put(key, value);
	}

}
