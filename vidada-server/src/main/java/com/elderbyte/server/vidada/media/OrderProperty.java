package com.elderbyte.server.vidada.media;

/**
 * Specifies all sort possibilities
 * @author IsNull
 *
 */
public enum OrderProperty
{
	/**
	 * Do not order at all
	 */
	NONE("Not ordered",""),

	/**
	 * Order by file-name
	 */
	TITLE("Title", "title"),

	/**
	 * Order by opened-count
	 */
	OPENED("Play count", "opened"),

	/**
	 * Order by added-date
	 */
	ADDEDDATE("Add Date", "addedDate"),

    /**
     * Order by last accessed date
     */
    LASTACCESS("Last Accessed", "lastAccessed"),

	/**
	 * Order by rating
	 */
	RATING("Rating", "rating"),

    /**
     * Order by file size
     */
    SIZE("File size", "fileSize"),

    /**
     * Order by duration
     */
    DURATION("Duration", "duration"),

    /**
     * Order by bitrate
     */
    BITRATE("Bitrate", "bitrate");



	private final String description;
	private final String property;

	OrderProperty(String description, String propertyName){
		this.description = description;
		this.property = propertyName;
	}

	public String getDescription() {
		return description;
	}

	public String getProperty() {
		return property;
	}


	@Override
	public String toString(){return getDescription();}
}
