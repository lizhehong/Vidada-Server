package com.elderbyte.vidada.media.extracted;

import com.elderbyte.vidada.media.MediaItem;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a property store in form of a file based json container.
 *
 * All properties for one media item are stored in a json file (which is a {@link PropertyContainer}),
 * the file name is the medias id (file hash).
 *
 * This implementation features a simple {@link PropertyContainer} cache so that subsequent
 * read access operations not repeatably require a read/parse of the json files.
 *
 */
public class JsonMediaPropertyStore implements IMediaPropertyStore {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(JsonMediaPropertyStore.class.getName());
    private static ObjectMapper mapper;

    private final Map<String, PropertyContainer> containerCache = new HashMap<>();
    private final File propertyStoreFolder;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new JsonMediaPropertyStore within the given folder location
     * @param propertyStoreFolder
     */
	public JsonMediaPropertyStore(File propertyStoreFolder){
		this.propertyStoreFolder = propertyStoreFolder;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**{@inheritDoc}*/
	@Override
	public String getProperty(MediaItem media, String key) {
		return getContainer(media).getProperty(key);
	}

    /**{@inheritDoc}*/
	@Override
	public void setProperty(MediaItem media, String key, String value) {
		PropertyContainer container = getContainer(media);
		container.setProperty(key, value);
		persistContainer(media, container);
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

	private File getContainerPath(MediaItem media){
		return new File(propertyStoreFolder, media.getFilehash() + ".json");
	}

	private void persistContainer(MediaItem media, PropertyContainer container){
        try {
            getMapper().writeValue(getContainerPath(media), container);
        } catch (IOException e) {
            logger.error("Could not write property container in json.",e);
        }
    }

	private PropertyContainer getContainer(MediaItem media){
		PropertyContainer container = containerCache.get(media.getFilehash());

		if(container == null){
			// try to read it from serialized file
			File containerPath = getContainerPath(media);
			if(containerPath.exists()){
				try {
					container = getMapper().readValue(containerPath, PropertyContainer.class);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// No existing properties could be found
			if(container == null){
				container = new PropertyContainer();
			}

			// Cache the container
			containerCache.put(media.getFilehash(), container);
		}
		return container;
	}

    /**
     * Gets the configured JSON mapper
     * @return
     */
	private synchronized static ObjectMapper getMapper(){
		if(mapper == null){
			mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		}
		return mapper;
	}
}
