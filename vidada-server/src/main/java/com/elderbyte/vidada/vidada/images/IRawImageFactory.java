package com.elderbyte.vidada.vidada.images;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * An abstract factory which creates a concrete instance of a IMemoryImage
 *
 * @author IsNull
 *
 */
public interface IRawImageFactory {

	/**
     * Creates a raw memory bitmap from the file path
	 */
	public abstract IMemoryImage createImage(File file);

	/**
	 * Creates a raw memory bitmap from the file path
	 */
	public abstract IMemoryImage createImage(URI uri);

	/**
	 * Creates a raw memory bitmap from the input stream
	 */
	public abstract IMemoryImage createImage(InputStream inputStream);

	/**
	 * Writes the given image into a new file
	 */
	public abstract boolean writeImage(IMemoryImage image, OutputStream outputStream);
}
