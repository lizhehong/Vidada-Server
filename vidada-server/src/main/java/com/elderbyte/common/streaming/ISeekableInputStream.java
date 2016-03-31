package com.elderbyte.common.streaming;

import java.io.IOException;

public interface ISeekableInputStream {

	/**
	 * @return total length of stream (file)
	 */
	long length() throws IOException;

	/**
	 * Seek within stream for next read-ing.
	 */
	void seek(long offset) throws IOException;
}
