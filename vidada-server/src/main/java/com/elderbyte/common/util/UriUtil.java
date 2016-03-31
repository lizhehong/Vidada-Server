package com.elderbyte.common.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UriUtil {


    public static URI createUri(File file){
        return file.toURI(); // TODO handle special cases where the file path contains already URI like prefix
    }

	/**
     * Ensures the given uri ends with a slash "/". If not, a slash is appended.
	 * @param uri
	 * @return
	 */
	public static URI ensureEndsWithSlash(URI uri){
		char[] uriStr = uri.toString().toCharArray();
		if(uriStr.length > 1){
			if(uriStr[uriStr.length-1] != '/'){
				try {
					uri = new URI(uri.toString() + "/");
				} catch (URISyntaxException e) {
					// Should never happen
				}
			}
		}
		return uri;
	}

	/**
	 *
	 * root:	smb:/host/my
	 * path:	path/blub
	 * ====>	smb:/host/my/path/blub
	 *
	 * @param root
	 * @param subPath The sub path should not start with a slash
	 * @return
	 */
	public static URI resolvePath(URI root, String subPath) throws URISyntaxException{
		String rootStr = root.toString();

		if(subPath.startsWith("/")){
			subPath = subPath.substring(1, subPath.length());
		}

		return new URI(rootStr + (!rootStr.endsWith("/") ? "/" : "") + subPath);
	}

	public static String resolveFileName(URI uri){
		String url = uri.toString();
		return url.substring( url.lastIndexOf('/')+1, url.length() );

	}

	public static String resolveFileNameNoExt(URI uri){
		String fileName = resolveFileName(uri);
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}



}
