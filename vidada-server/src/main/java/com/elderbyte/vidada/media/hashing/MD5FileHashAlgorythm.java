package com.elderbyte.vidada.media.hashing;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



	public class MD5FileHashAlgorythm implements IFileHashAlgorythm {




		@Override
		public byte[] calculateHash(File file) {
			byte[] md5 = null;

			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}


			if(md != null && file.exists())
			{
				InputStream is;
				try {

					is = new FileInputStream(file);
					byte[] buffer=new byte[8192];
					int read=0;
					while( (read = is.read(buffer)) > 0)
						md.update(buffer, 0, read);

					md5 = md.digest();


				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return md5;
		}

		@Override
		public String calculateHashString(File file) {
			byte[] md5 = this.calculateHash(file);
			return  toString(md5);
		}

		private static String toString(byte[] hash){
			BigInteger bi=new BigInteger(1, hash);
			return  bi.toString(16);
		}

		@Override
		public String calculateHashString(InputStream file, long length) {
			byte[] md5 = calculateHash(file, length);
			return  toString(md5);
		}


		public byte[] calculateHash(InputStream is, long length) {

			byte[] md5 = null;

			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}


			if(md != null && is != null)
			{
				try {
					byte[] buffer=new byte[8192];
					int read=0;
					while( (read = is.read(buffer)) > 0)
						md.update(buffer, 0, read);

					md5 = md.digest();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return md5;
		}

	}
