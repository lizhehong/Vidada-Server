package com.elderbyte.vidada.domain.media;

import com.elderbyte.vidada.domain.media.source.MediaSource;

import javax.persistence.Entity;
import java.net.URI;

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

	transient private final static int MAX_THUMB_RETRY_COUNT = 2;

	private float thumbnailPosition = 0.35f;

	private volatile int thumbCreationFails = 0;
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

    /**
     * Returns a clone of this instance
     * @return
     */
    @Override
    public MovieMediaItem clone() {
        return new MovieMediaItem(this);
    }

    /***************************************************************************
     *                                                                         *
     * Public Properties                                                       *
     *                                                                         *
     **************************************************************************/

	public int getThumbCreationFails() {
		return thumbCreationFails;
	}

	protected void setThumbCreationFails(int thumbCreationFails) {
		this.thumbCreationFails = thumbCreationFails;
	}

	public void onThumbCreationFailed(){
		// increment failure counter
		setThumbCreationFails(getThumbCreationFails()+1);
	}

	public void onThumbCreationSuccess(){
		setThumbCreationFails(0);
	}

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
		return thumbCreationFails <= MAX_THUMB_RETRY_COUNT  && isAvailable();
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
        setThumbnailPosition(0);
        setBitrate(prototype.getBitrate());
        setDuration(prototype.getDuration());
    }

    /**
     * Returns a random relative position for a movie
     * This method ensures that the position is not too near
     * of the start or end of the movie.
     *
     * (Lots of movies have a boring screener at the start
     * and even more boring credits listing at the end.)
     *
     * @return
     */
    public static float randomRelativePos(){
        float pos = (float)Math.random();
        pos = Math.max(pos, 0.08f);
        pos = Math.min(pos, 0.90f);
        return pos;
    }


}
