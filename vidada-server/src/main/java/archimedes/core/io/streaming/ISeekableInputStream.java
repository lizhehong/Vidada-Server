package archimedes.core.io.streaming;

import java.io.IOException;

public interface ISeekableInputStream {

	/**
	 * @return total length of stream (file)
	 */
	public abstract long length() throws IOException;

	/**
	 * Seek within stream for next read-ing.
	 */
	public abstract void seek(long offset) throws IOException;
}
