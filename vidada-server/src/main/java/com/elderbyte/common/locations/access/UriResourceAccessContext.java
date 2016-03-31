package com.elderbyte.common.locations.access;

import com.elderbyte.common.locations.ResourceAccessContext;

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

