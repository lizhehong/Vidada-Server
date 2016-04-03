package com.elderbyte.vidada.media;

import com.elderbyte.vidada.media.source.MediaSource;

import javax.persistence.Entity;

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
	 * @param hash
	 */
	public ImageMediaItem(MediaSource source, String hash) {
		super(source);
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

}
