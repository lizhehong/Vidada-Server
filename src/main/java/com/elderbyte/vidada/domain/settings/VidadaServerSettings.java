package com.elderbyte.vidada.domain.settings;

import archimedes.core.geometry.Size;
import archimedes.core.util.OSValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class VidadaServerSettings {


    public static final String ProductName = "vidada-server";


    @Value("metadata.fileAttributes.enabled")
    private boolean usingMetaData;

    @Value("vidada.debug.enabled")
    private boolean isDebug;

    @Value("vidada.thumbnails.maxWidth")
    private int maxThumbnailWidth;

    @Value("vidada.thumbnails.aspectRatio")
    private double thumbAspectRatio;

	// thumb size boundaries
	//transient public static final int THUMBNAIL_SIZE_MAX = 500;
	//transient public static final double THUMBNAIL_SIDE_RATIO = 0.70;


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
	public Size getMaxThumbResolution(){
		int maxWidth = getMaxThubnailWidth() * (OSValidator.isHDPI() ? 2 : 1);
		return new Size(
				maxWidth,
				(int)((double)maxWidth*thumbnailAspectRatio()));
	}



	/**
	 * Determites if meta data is used to help identify files
	 * and store additional informations
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

	/***************************************************************************
	 *                                                                         *
	 * Private static methods                                                  *
	 *                                                                         *
	 **************************************************************************/


	private static File toAbsolutePath(String path){
		return toAbsolutePath(new File(path));
	}

	private static File toAbsolutePath(File path){
		if(path == null) return null;

		if(path.isAbsolute())
		{
			return path;
		}else {
			return new File(".", path.getPath());
		}
	}


}
