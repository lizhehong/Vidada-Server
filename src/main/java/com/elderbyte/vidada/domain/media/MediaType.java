package com.elderbyte.vidada.domain.media;

import archimedes.core.enums.EnumConverter;
import archimedes.core.enums.ReverseEnumMap;

/**
 * Lists all available media types
 * @author IsNull
 */
public enum MediaType implements EnumConverter<MediaType> {

	NONE(0x00, "None"), ANY(0xFF,"All"), MOVIE(0x01,"Movies"), IMAGE(0x02,"Images");

	private final byte id;
	private final String displayName;
	transient private final static ReverseEnumMap<MediaType> map =
			new ReverseEnumMap<MediaType>(MediaType.class);

	MediaType(int id, String displayName){
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
	public MediaType convert(byte val) {
		return map.get(val);
	}
}
