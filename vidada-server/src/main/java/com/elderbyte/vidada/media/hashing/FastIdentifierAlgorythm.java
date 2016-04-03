package com.elderbyte.vidada.media.hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



/********************************************************************************
 *
 * <h1>FastIdentifierAlgorythm </h1>
 * <h2> A fast hash based identification algorithm for large media files</h2>
 *
 * <p>
 * This hash based identification algorithm is a trade-off between correctness
 * and performance.
 * The algorithm reads at max the first [n] bytes of the input data,
 * and appends the file size to that data sample and calculates the MD5 hash
 * of it.
 * <p>
 * As in most cases, header information is stored at the beginning,
 * the start section seems to be the most identifying part.
 * Thus the identification is based upon the file header and
 * the first bytes of the content.
 * To prevent most simple collisions, the file size is part of the sample data.
 * <p>
 * <h2>Further thoughts</h2>
 * <p>
 * Reading only a few bytes throughout the file (such as every 1MB read 250Bytes)
 * seems to be considerable idea, but it is tricky to avoid loading the whole file
 * from the hard disk but still provide a good performance.
 * <p>
 * SSDs may change this in future, so a more precise but still fast version can be
 * developed.
 * <p>
 *
 *
 * <h2>Standards for sample size [n]:</h2>
 * <p>
 * <ul>
 * <li>BHA-512		// data sample of the first 512 bytes
 * <li>BHA-4096		// data sample of the first 4096 bytes (16*256)
 * </ul>
 *
 * @author IsNull
 *
 */
public class FastIdentifierAlgorythm implements IFileHashAlgorythm {

	public final static int  Sample4096 = 4096;
    public final static int  Sample512 = 512;

	private final int bufferSize = Sample4096;
	private final int sampleSize;

	private final MessageDigest md;


	/**
	 * Creates a new Instance of ButtikscheIdentifierAlgorythm with the given sample size
	 *
	 * @param sampleSize Sample size to use
	 */
	public FastIdentifierAlgorythm(int sampleSize) {
		this.sampleSize = sampleSize;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] calculateHash(File file) {
		byte[] hash = null;
		InputStream is = null;

		try {
			is = new FileInputStream(file);
			hash = calculateHash(is, file.length());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(is != null)
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return hash;
	}


	@Override
	public String calculateHashString(File file) {
		byte[] md5 = this.calculateHash(file);
		return toString(md5);
	}

	private static String toString(byte[] hash){
		BigInteger bi=new BigInteger(1, hash);
		return  bi.toString(16);
	}

	@Override
	public String calculateHashString(InputStream file, long length) {
		byte[] md5 = calculateHash(file, length);
		return toString(md5);
	}


	public byte[] calculateHash(InputStream is, long length) {
		byte[] hash = null;

		if(md != null && is != null)
		{
			try {
				int bytesRead = 0;

				byte[] buffer = new byte[bufferSize];

				// read the sample bytes
				while (sampleSize > bytesRead) {
					int read = is.read(buffer);
					if(read >= 0){
						md.update(buffer, 0, read);
						bytesRead += read;

						if(read < buffer.length) break; // eof reached before full sample size
					}else
						break; // could not read a single byte
				}


				//
				// add the size to the md pool
				//
				long lenght = length;
				byte[] lenghtbytes = ByteBuffer.allocate(8).putLong(lenght).array();
				md.update(lenghtbytes, 0, lenghtbytes.length);

				hash = md.digest();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hash;
	}

}

