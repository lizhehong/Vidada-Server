package com.elderbyte.common.locations.factories;

import com.elderbyte.common.locations.Credentials;
import com.elderbyte.common.locations.DirectoryLocation;
import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.locations.http.HttpResourceLocation;
import com.elderbyte.common.locations.local.LocalResourceLocation;
import com.elderbyte.common.NotSupportedException;
import com.elderbyte.common.util.UriUtil;


import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class ResourceLocationFactory   {


	public ResourceLocation create(File file) {
		return new LocalResourceLocation(file, null);
	}

	/*
	public ResourceLocation create(String uri) throws URISyntaxException{
		return create(uri, null);
	}


	public ResourceLocation create(String uri, Credentials credentials) throws URISyntaxException{
        return create(UriUtil.createUri(uri), credentials);
	}*/


	public ResourceLocation create(URI uri, Credentials credentials) {

        String scheme = uri.getScheme();

        if(scheme != null){
            if(scheme.equals("http")) {
                return new HttpResourceLocation(uri, credentials);
            }else if(scheme.equals("file")){
                return new LocalResourceLocation(uri, credentials);
            }else{
                throw new NotSupportedException("The scheme '" + scheme + "' is not supported!");
            }
        }else{
            // In case the given URI has no scheme
            // try to use the given path directly as a local file path
			return new LocalResourceLocation(uri, credentials);
		}
	}


	public ResourceLocation create(DirectoryLocation root, String relativePath)
			throws URISyntaxException {
		return create(UriUtil.resolvePath(root.getUri(), relativePath), root.getCredentials());
	}
}
