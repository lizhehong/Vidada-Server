package com.elderbyte.server.vidada.media;


/**
 * Filter for media types
 */
public enum MediaFilterType {

    /**
     * Matches everything
     */
    ANY(0xFF,"All"),

    /**
     * Matches videos
     */
    MOVIE(0x01,"Videos"),

    /**
     * Matches images
     */
    IMAGE(0x02,"Images");

    private final byte id;
    private final String displayName;

    MediaFilterType(int id, String displayName){
        this.id = (byte)id;
        this.displayName = displayName;
    }

    @Override
    public String toString(){
        return displayName;
    }

}

