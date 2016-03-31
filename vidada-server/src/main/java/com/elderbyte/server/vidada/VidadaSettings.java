package com.elderbyte.server.vidada;

import com.elderbyte.common.util.OSValidator;
import com.elderbyte.server.vidada.media.Resolution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides a typed access to the application settings
 * defined in /resources/config/application.yml
 */
@Component
public class VidadaSettings {


    public static final String ProductName = "vidada-server";


    @Value("${vidada.metadata.fileAttributes.enabled}")
    private boolean usingMetaData;

    @Value("${vidada.debug.enabled}")
    private boolean isDebug;

    @Value("${vidada.thumbnails.maxWidth}")
    private int maxThumbnailWidth;

    @Value("${vidada.thumbnails.aspectRatio}")
    private double thumbAspectRatio;



    public int getMaxThubnailWidth(){
        return maxThumbnailWidth;
    }

    public double thumbnailAspectRatio(){
        return thumbAspectRatio;
    }

	/**
	 * Returns the max thumb resolution used in the whole application
	 * (HDPI aware)
	 * @return
	 */
	public Resolution getMaxThumbResolution(){
		int maxWidth = getMaxThubnailWidth() * (OSValidator.isHDPI() ? 2 : 1);
		return new Resolution(
				maxWidth,
				(int)((double)maxWidth*thumbnailAspectRatio()));
	}

	/**
	 * Determites if meta data is used to help identify files
	 * and save additional informations
	 * @return
	 */
	public boolean isUsingMetaData() {
		return usingMetaData;
	}


	/**
	 * Is this app currently running in Debug mode?
	 * @return
	 */
	public boolean isDebug() {
		return isDebug;
	}

	/**
	 * Set the debug mode of this App
	 * @param isDebug
	 */
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

}
