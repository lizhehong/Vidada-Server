package com.elderbyte.vidada.media;


import archimedes.core.enums.EnumConverter;
import archimedes.core.enums.ReverseEnumMap;


/**
 * Filter for media types
 */
public enum MediaFilterType implements EnumConverter<MediaFilterType> {

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
    transient private final static ReverseEnumMap<MediaFilterType> map = new ReverseEnumMap<>(MediaFilterType.class);

    MediaFilterType(int id, String displayName){
        this.id = (byte)id;
        this.displayName = displayName;
    }

    @Override
    public String toString(){
        return displayName;
    }


    @Override
    public byte convert() {
        return id;
    }

    @Override
    public MediaFilterType convert(byte val) {
        return map.get(val);
    }
}

