package com.elderbyte.common.locations;


import com.elderbyte.common.NotSupportedException;
import com.elderbyte.common.util.OSValidator;

import javax.persistence.Transient;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;


/**
 * Represents a location of a file, folder or more generally any service identified by an URI
 * This might be a local or a network resource.
 *
 * @author IsNull
 */
public abstract class UniformLocation {

	private URI uri;
	private Credentials credentials;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new UniformLocation
     * @param uri
     * @param credentials
     */
	protected UniformLocation(URI uri, Credentials credentials) {
		this.uri = uri;
		this.credentials = credentials;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


	/**
	 * Gets the Uri to this location
	 * @return
	 */
	public URI getUri() {
		return uri;
	}

	public void setUri(URI url) {
		this.uri = url;
	}

	/**
	 * Gets the authentication to access this location
	 * @return
	 */
	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * Gets the location name
	 * @return
	 */
	@Transient
	public abstract String getName();

	/**
	 * Checks if the location is available
	 * @return
	 */
	public abstract boolean exists() throws IOException;

	/**
	 * Does this location have the hidden flag?
	 * @return
	 */
	@Transient
	public abstract boolean isHidden() throws IOException;


	/**
	 * Creates all directories to this path
	 */
	public abstract boolean mkdirs() throws IOException;

	/**
	 * Try to delete this location
	 * @return
	 */
	public abstract boolean delete() throws IOException;

	/**
	 * Returns the parent of this location or <code>null</code> if no
	 * parent is available
	 * @return
	 */
	@Transient
	public abstract DirectoryLocation getParent();


	@Override
	public String toString(){
		return getUriString();
	}

	/**
	 * Returns the decoded Uri string.
	 * (I.e. contains normal spaces instead of %20 encodings)
	 *
	 * @return
	 */
	@Transient
	public String getUriString(){
		try {
			return URLDecoder.decode(getUri().toString(),  "utf-8");
		} catch (UnsupportedEncodingException e) {
			// Should never happen!
		}
		return null;
	}

	/**
	 * Returns a decoded path to this resource.
	 * This can be a URI but in case of local paths
	 * it will return a file path compatible with
	 * the current operating system.
	 *
	 * @return
	 */
	public String getPath(){
		String resourcePath = "";
		try {
			URI resource = this.getUri();
			if("file".equals(resource.getScheme())){
				resourcePath = URLDecoder.decode(resource.toString(), "utf-8");
				resourcePath = new File(resourcePath).getPath();
				resourcePath = resourcePath.replace("file:", "");
				if(OSValidator.isWindows()){
					if(!resourcePath.isEmpty() &&
							(resourcePath.toCharArray()[0] == '\\' || resourcePath.toCharArray()[0] == '/')){
						resourcePath = resourcePath.substring(1);
					}
				}
			}else{
				resourcePath = this.getUriString();
			}
		} catch (UnsupportedEncodingException e) {
			// Should never happen!
		}
		return resourcePath.trim();
	}

    /***************************************************************************
     *                                                                         *
     * Static methods                                                          *
     *                                                                         *
     **************************************************************************/


    /**
     * Ensures the the given uri has the given scheme
     * @param uri
     * @param scheme
     * @return
     */
	protected static URI ensureScheme(URI uri, String scheme) {
		URI validUri = uri;
		if(scheme != null){
			String currentScheme = uri.getScheme();
			if(currentScheme == null || currentScheme.length() == 0){
				// no scheme given - fix it
				try {
					validUri = new URI(scheme + ":" + uri.toString());
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}else{
				if(currentScheme.equals(scheme)){
					validUri = uri;
				}else
					throw new NotSupportedException("Invalid scheme for LocalDirectoryLocation: " + currentScheme);
			}
		}
		return validUri;
	}

    /**
     * Ensures the given URI has a ending slash (denotes a directory and not a file)
     * @param uri
     * @return
     */
    protected static URI ensureEndingSlash(URI uri){

        if(uri != null){
            String rawUri = uri.toString();
            if(!rawUri.endsWith("/")){
                try {
                    return new URI(rawUri + "/");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

        return uri;
    }

    /***************************************************************************
     *                                                                         *
     * Equality members                                                        *
     *                                                                         *
     **************************************************************************/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((credentials == null) ? 0 : credentials.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniformLocation other = (UniformLocation) obj;
		if (credentials == null) {
			if (other.credentials != null)
				return false;
		} else if (!credentials.equals(other.credentials))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
