package com.elderbyte.common.locations.http;

import com.elderbyte.common.locations.Credentials;
import com.elderbyte.common.locations.DirectoryLocation;
import com.elderbyte.common.locations.IResourceAccessContext;
import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.locations.access.UriResourceAccessContext;
import com.elderbyte.common.NotSupportedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class HttpResourceLocation extends ResourceLocation {

	public HttpResourceLocation(URI uri, Credentials creditals) {
		super(uri, creditals);
	}

	@Override
	public InputStream openInputStream() {
		InputStream input = null;
		try {
			// TODO Random access support
			// TODO authentication support...
			input = getUri().toURL().openStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	@Override
	public OutputStream openOutputStream() {
		// TODO Implement
		throw new NotSupportedException("HTTP Location does not support writing");
	}

	@Override
	public IResourceAccessContext openResourceContext() {
		return new UriResourceAccessContext(getUri());
	}

	@Override
	public long length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean mkdirs() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DirectoryLocation getParent() {
		// TODO Auto-generated method stub
		return null;
	}

}
