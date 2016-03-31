package archimedes.core.io.locations.access;

import archimedes.core.io.locations.ResourceAccessContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class FileResourceAccessContext extends ResourceAccessContext {

	private final File localFile;

	protected FileResourceAccessContext(URI uri) {
		this(uri, new File(uri));
	}

	public FileResourceAccessContext(URI uri, File file) {
		super(uri);
		localFile = file;
	}

	@Override
	public boolean isOpen() {
		return localFile.exists();
	}

	@Override
	public void close() throws IOException {
		// Don't do anything...
	}

}
