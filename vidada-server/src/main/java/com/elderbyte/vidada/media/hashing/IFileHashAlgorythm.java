package com.elderbyte.vidada.media.hashing;

import java.io.File;
import java.io.InputStream;

public interface IFileHashAlgorythm {

	/**
     * Calculates the hash of the given File and returns the hash-bytes
	 * @param file
	 * @return
	 */
	public byte[] calculateHash(File file);

	/**
	 * Calculates the hash of the given File and returns the hash as string
	 * @param file
	 * @return
	 */
	public String calculateHashString(File file);


	/**
	 * Calculates the hash of the given File and returns the hash as string
	 * @param file
	 * @return
	 */
	public String calculateHashString(InputStream file, long length);

}
