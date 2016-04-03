package com.elderbyte.vidada.images;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Represents an abstract decorator for an image in memory.
 * @author IsNull
 *
 */
public interface IMemoryImage {

	/**
     * Gets the platform specific image
	 * @return
	 */
	Object getOriginal();

	/**
	 * Gets the image width
	 * @return
	 */
	int getWidth();

	/**
	 * Gets the image height
	 * @return
	 */
	int getHeight();

	/**
	 * Rescale this image to the desired size and return the new rescaled image
	 * @param width
	 * @param heigth
	 * @return
	 */
	IMemoryImage rescale(int width, int heigth);

	/**
	 * Write this image as PNG into the given stream
	 */
	void writePNG(OutputStream outputstream) throws IOException;

}
