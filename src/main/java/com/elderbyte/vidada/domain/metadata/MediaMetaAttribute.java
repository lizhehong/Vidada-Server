package com.elderbyte.vidada.domain.metadata;

/**
 * Known Vidada MetaData Attributes.
 * @author IsNull
 *
 */
public enum MediaMetaAttribute {

	/**
	 * Default vidada hash id
	 *
	 */
	FileHash("vidada.hash"),

	/**
	 * The tags of this media
	 */
	Tags("vidada.tags"),

	/**
	 * The rating of this media
	 */
	Rating("vidada.rating"),

    /**
     * The resolution of this media in pixel: 500x200
     */
    Resolution("vidada.resolution"),

    /**
     * The duration of this media (videos and sound)
     */
    Duration("vidada.duration"),

    /**
     * The bitrate of this media (videos and sound)
     */
    Bitrate("vidada.bitrate"),

	;

	private String attributeName;

    /**
     * Enum Constructor
     * @param attributeName
     */
	private MediaMetaAttribute(String attributeName){
        this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}

}
