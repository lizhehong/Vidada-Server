package com.elderbyte.vidada.domain.media;

import javax.persistence.Entity;
import java.net.URI;

@Entity
public class ImageMediaItem extends MediaItem {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	/**
	 * ORM Constructor
	 */
	protected ImageMediaItem() {
		classinfo = "image";
	}

	/**
	 * Creates a copy of the given prototype
	 * @param prototype
	 */
	public ImageMediaItem(ImageMediaItem prototype){
		this();
		prototype(prototype);
	}

	/**
	 * Create a new Imagepart
	 *
	 * @param parentLibrary
	 * @param relativeFilePath
	 * @param hash
	 */
	public ImageMediaItem(MediaLibrary parentLibrary, URI relativeFilePath, String hash) {
		super(parentLibrary, relativeFilePath);
		setFilehash(hash);
		setType(MediaType.IMAGE);
		classinfo = "image";
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    public void prototype(ImageMediaItem prototype) {

        // Copy ImageMediaItem fields here

        super.prototype(prototype);
    }

    @Override
    public void prototype(MediaItem prototype) {
        if(prototype instanceof ImageMediaItem) {
            prototype((ImageMediaItem) prototype);
        }else{
            super.prototype(prototype);
        }
    }

	@Override
	public ImageMediaItem clone() {
		return new ImageMediaItem(this);
	}

}
