package com.elderbyte.vidada.domain.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 * Base class for Json based settings
 * @author IsNull
 *
 */
public class JsonSettings {

    transient private static final Logger logger = LogManager.getLogger(JsonSettings.class.getName());


    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/


    transient private File settingsPath = null;
	private String settingsVersion = "1.0";

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

	protected JsonSettings() { }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

	public String getSettingsVersion(){
		return settingsVersion;
	}

	protected void setSettingsVersion(String version){
		settingsVersion = version;
	}

	protected void setPath(File path){
		this.settingsPath = path;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    /**
     * Save the changes in the configuration
     */
	public void persist() {

		assert settingsPath != null : "You must set the Settings path before calling persist()!";

		ObjectMapper mapper = buildMapper();

		try {
			mapper.writeValue(settingsPath, this);
		} catch (JsonGenerationException e) {
            logger.error(e);
		} catch (JsonMappingException e) {
            logger.error(e);
		} catch (IOException e) {
            logger.error(e);
		}
	}

    /***************************************************************************
     *                                                                         *
     * Static methods                                                          *
     *                                                                         *
     **************************************************************************/

    /**
     * Loads a configuration file
     * @param path Path to the json file
     * @param type Type of the configuration
     * @param <T>
     * @return
     */
	public static <T> T loadSettings(File path, Class<T> type){
		T settings = null;
		try {
			ObjectMapper mapper = buildMapper();
			settings = mapper.readValue(path, type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return settings;
	}

	private static ObjectMapper buildMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return mapper;
	}


}
