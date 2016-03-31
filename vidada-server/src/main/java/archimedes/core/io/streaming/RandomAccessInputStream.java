package archimedes.core.io.streaming;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Seekable InputStream
 */
public abstract class RandomAccessInputStream extends FilterInputStream implements ISeekableInputStream {

	protected RandomAccessInputStream(InputStream original) {
		super(original);
	}

	/**
	 * @return total length of stream (file)
	 */
	@Override
	public abstract long length() throws IOException;

	/**
	 * Seek within stream for next read-ing.
	 */
	@Override
	public abstract void seek(long offset) throws IOException;

	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		read(b);
		return b[0] & 0xff;
	}
}
