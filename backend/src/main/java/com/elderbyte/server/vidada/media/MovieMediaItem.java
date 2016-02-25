package com.elderbyte.server.vidada.media;

import com.elderbyte.server.vidada.media.source.MediaSource;

import javax.persistence.Entity;

/**
 * Represents a playable MoviePart. A full Movie can be composed of multiple
 * MovieParts
 *
 * @author IsNull
 *
 */
@Entity
public class MovieMediaItem extends MediaItem implements Cloneable {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private float thumbnailPosition = 0.35f;
	private int bitrate;
	private int duration;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	/**
	 * ORM constructor
	 */
	MovieMediaItem() {
		classinfo = "movie";
	}


    /**
     *
     * @param source
     * @param hash
     */
    public MovieMediaItem(MediaSource source, String hash) {
        super(source);
        setFilehash(hash);
        setType(MediaType.MOVIE);
        classinfo = "movie";
    }

	/**
	 * Copy constructor
	 *
	 * @param prototype
	 */
	public MovieMediaItem(MovieMediaItem prototype) {
		this();
		prototype(prototype);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    @Override
    public void prototype(MediaItem prototype) {
        if(prototype instanceof MovieMediaItem) {
            prototype((MovieMediaItem) prototype);
        }
        super.prototype(prototype);
    }

    /***************************************************************************
     *                                                                         *
     * Public Properties                                                       *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the relative thumbnail position [0.0 - 1.0]
     * @return
     */
    public float getThumbnailPosition() {
        return thumbnailPosition;
    }

    /**
     * Sets the relative thumbnail position
     * @param thumbnailPosition [0.0 - 1.0]
     */
    public void setThumbnailPosition(float thumbnailPosition) {
        this.thumbnailPosition = thumbnailPosition;
    }

	/**
	 * Is it possible to create a thumb of this movie part?
	 *
	 * Generally, the file must be available and a video encoder for this format
	 * must be present, lastly previous fails must be below <code>MAX_THUMB_RETRY_COUT</code>
	 *
	 */
	public boolean canCreateThumbnail() {
		// Ensure we do not try forever when we deal with defect videos
		return isAvailable();
	}



	/**
	 * Set the bitrate in Kilo bits (Kb)
	 * @param bitRate
	 */
	public void setBitrate(int bitRate) {
		this.bitrate = bitRate;
	}

	/**
	 * Gets the bitrate in Kilo bits per second
	 * @return
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * Gets the duration in seconds
	 * @return
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration in seconds
	 * @param duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

    /**
     * Copy all values from prototype to this instance.
     * @param prototype
     */
    private void prototype(MovieMediaItem prototype) {
        setThumbnailPosition(prototype.getThumbnailPosition());
        setBitrate(prototype.getBitrate());
        setDuration(prototype.getDuration());
    }

}
