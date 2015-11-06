package com.elderbyte.vidada.domain.connectivity;


import java.util.List;

/***
 * Represents a collection of media data information
 * for exchange and easy serialization
 *
 * @author IsNull
 *
 */
@Deprecated
public class MediaDataInfoPack {


	public MediaDataInfoPack(){}

	public MediaDataInfoPack(List<MediaDataInfo> medias)
	{
		this.Medias = medias;
	}


	/**
	 * Protocol version
	 */
	public String version = "1.0";

	/**
	 *
	 */
	public List<MediaDataInfo> Medias;
}
