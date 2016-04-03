package com.elderbyte.vidada.media.source;

import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.vidada.entities.IdEntity;
import com.elderbyte.vidada.media.MediaType;
import com.elderbyte.vidada.media.MediaTypeUtil;
import com.elderbyte.vidada.media.libraries.MediaLibrary;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
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
	private MediaLibrary parentLibrary;

    @NotNull
    @Size(max = 1000)
    @Column(length = 1000)
    private String relativePathUri;

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
		return location != null ? location.getName() : "<no name>";
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
				logger.error("Parent library is null of " + this.relativePathUri);
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
			return new URI(relativePathUri);
		} catch (URISyntaxException e) {
			logger.error("relativepath URI was invalid!", e);
			return null;
		}
	}

	public void setRelativePath(URI relativePath) {
        if(relativePath == null) throw new IllegalArgumentException("relativePath");
		this.relativePathUri = relativePath.toString();
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
        try {
            return absolutePath != null && absolutePath.exists();
        } catch (IOException e) {
            logger.warn("MediaSource is not available - exception:", e);
            return false;
        }
    }


    /**
     * Returns the mime type for this media source
     * @return
     */
    public String getMimeType() {

        String mimeType;
        String extensionWithoutDot = getResourceLocation().getExtension().substring(1).toLowerCase();

        switch (findMediaType()){
            case MOVIE:
                mimeType = "video/" + extensionWithoutDot;
                break;
            case IMAGE:
                mimeType = "image/" + extensionWithoutDot;
                break;

            default:
                return "application/octet-stream";
        }
        return mimeType;
    }

    /**
     * Gets the media type of this source
     * @return
     */
    public MediaType findMediaType(){
        return MediaTypeUtil.findTypeByResource(getResourceLocation());
    }

}
