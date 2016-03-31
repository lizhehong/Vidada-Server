package archimedes.core.io.locations.factories;

import archimedes.core.io.locations.Credentials;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.local.LocalDirectoryLocation;
import com.elderbyte.common.NotSupportedException;
import com.elderbyte.common.util.UriUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


public class DirectoryLocationFactory {

	/*
	public DirectoryLocation create(String uri) throws URISyntaxException {
		return create(uri, null);
	}

	public DirectoryLocation create(String uri, Credentials creditals)
			throws URISyntaxException {
        URI myUri = UriUtil.createUri(uri);
        return create(myUri, creditals);
	}
	*/

	public DirectoryLocation create(URI uri) {
		return create(uri, null);
	}

	public DirectoryLocation create(URI uri, Credentials creditals) {
		String scheme = uri.getScheme();

		if(scheme != null){
			if(scheme.equals("file")){
				return new LocalDirectoryLocation(uri, creditals);
			}else{
                throw new NotSupportedException("The scheme '" + scheme + "' is not supported!");
            }
		}else{
			// In case the given URI has no scheme
			// try to use the given path directly as a local file path
			return new LocalDirectoryLocation( new File(uri.getPath()), creditals);
		}
	}

	public DirectoryLocation create(File file) {
		return new LocalDirectoryLocation(file, null);
	}


	public DirectoryLocation create(DirectoryLocation root, String subDirs) throws URISyntaxException {
		return create(
            UriUtil.resolvePath(root.getUri(), subDirs),
				root.getCredentials());
	}



}
