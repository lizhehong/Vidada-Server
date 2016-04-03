package com.elderbyte.vidada.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;



public class FileRandomAccessInputStream extends RandomAccessInputStream {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final RandomAccessFile file;


	public FileRandomAccessInputStream(RandomAccessFile file) {
		super(null);
		if(file == null) throw new IllegalArgumentException("file must not be Null!");
		this.file = file;
	}

	@Override
	public boolean markSupported(){
		return false;
	}

	@Override
	public void close() throws IOException{
		file.close();
	}


	@Override
	public int read() throws IOException {
		return file.read();
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		return file.read(bytes);
	}

	@Override
	public int read(byte[] bytes, int offset, int len)  throws IOException{
		return file.read(bytes, offset, len);
	}

	@Override
	public long length() throws IOException {
		return file.length();
	}

	@Override
	public void seek(long offset) throws IOException {
		file.seek(offset);
	}

	@Override
	public long skip(long bytes) throws IOException {
		return this.file.skipBytes((int)bytes);
	}

	@Override
	public int available() throws IOException {
		return (int) (length() - file.getFilePointer());
	}


	@Override
	public synchronized void mark(int readlimit) {
		// not supported
	}

	@Override
	public void reset() throws IOException{
		throw new IOException("mark/reset not supported");
	}



}
