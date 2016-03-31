package archimedes.core.io.locations.access;

import archimedes.core.io.locations.ResourceAccessContext;

import java.io.IOException;
import java.net.URI;

public class UriResourceAccessContext extends ResourceAccessContext {

	public UriResourceAccessContext(URI uri) {
		super(uri);
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void close() throws IOException {
		// Don't do anything...
	}
}

