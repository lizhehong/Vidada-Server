package com.elderbyte.vidada.domain.media.source;

import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.domain.entities.IdEntity;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents a media source.
 * This can be a local file, a web resource, smb shared resource or anything other which can deliver a stream.
 *
 * @author IsNull
 *
 */
@Entity
public class MediaSource extends IdEntity {

	/***************************************************************************
	 *                                                                         *
	 * Private Fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	transient private static final Logger logger = LogManager.getLogger(MediaSource.class.getName());

	@ManyToOne
	private MediaLibrary parentLibrary = null;
	private String relativePath = null;

	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	/** ORM Constructor */
	protected MediaSource(){ }

	/**
	 * Creates a new MediaSource
	 * @param parentLibrary
	 * @param relativePath
	 */
	public MediaSource(MediaLibrary parentLibrary, URI relativePath)
	{
		if(parentLibrary == null)
			throw new IllegalArgumentException("parentLibrary");
		if(relativePath == null)
			throw new IllegalArgumentException("relativePath");


		setParentLibrary(parentLibrary);
		setRelativePath(relativePath);
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	public String getName() {
		ResourceLocation location = getResourceLocation();
		return location != null ? location.getName() : "AbsoluteFilePath == NULL";
	}


	// ----------------

	public ResourceLocation getResourceLocation() {
		ResourceLocation absolutePath = null;
		URI relativePath = getRelativePath();
		if(relativePath != null){
			MediaLibrary parentLib = getParentLibrary();
			if(parentLib != null){
				absolutePath = parentLib.getMediaDirectory().getAbsolutePath(relativePath);
			}else
				logger.error("Parent library is null of " + this.relativePath);
		}else {
			logger.error("getResourceLocation: relativePath is NULL!");
		}
		return absolutePath;
	}

	/**
	 * Gets the relative file path of this item. The relative path is based upon
	 * the media library path.
	 *
	 * @return
	 */
	public URI getRelativePath() {
		try {
			return new URI(relativePath);
		} catch (URISyntaxException e) {
			logger.error(e);
			return null;
		}
	}

	public void setRelativePath(URI relativePath) {
		this.relativePath = relativePath.toString();
	}

	/**
	 * Get the parent library which holds this media source
	 * @return
	 */
	public MediaLibrary getParentLibrary() {
		return parentLibrary;
	}

	protected void setParentLibrary(MediaLibrary parentLibrary) {
		this.parentLibrary = parentLibrary;
	}


	@Override
	public String toString(){
		return "[" + getName() + "] available: " + isAvailable();
	}

	/**
	 * Is the resource available?
	 * @return
	 */
	public boolean isAvailable(){
		ResourceLocation absolutePath = getResourceLocation();
		return absolutePath != null && absolutePath.exists();
	}
}