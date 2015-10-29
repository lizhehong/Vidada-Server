package com.elderbyte.vidada.domain.media;

import archimedes.core.data.observable.IObservableCollection;
import archimedes.core.data.observable.ObservableCollection;
import archimedes.core.geometry.Size;
import archimedes.core.util.Lists;
import com.elderbyte.vidada.domain.entities.BaseEntity;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import com.elderbyte.vidada.domain.tags.Tag;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.beans.Transient;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single media item. Base class of all media items.
 *
 * @author IsNull
 *
 */
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "classinfo")
@JsonSubTypes({
	@Type(value = MovieMediaItem.class, name = "movie"),
	@Type(value = ImageMediaItem.class, name = "image") })
@Entity
public abstract class MediaItem extends BaseEntity {

	/***************************************************************************
	 *                                                                         *
	 * Private persistent fields                                               *
	 *                                                                         *
	 **************************************************************************/

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<MediaSource> sources = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	private Set<Tag> tags = new HashSet<>();

	@Id
	private String filehash = null;
	private String filename = null;
	private DateTime addedDate = new DateTime();
    private long fileSize = -1;
	private Size resolution = Size.Empty;
	private int opened = 0;
	private int rating = 0;
	private MediaType type = MediaType.UNKNOWN;
    private DateTime lastAccessed = new DateTime();



    @javax.persistence.Transient // Only used for JSON serialisation
	protected String classinfo;

	/***************************************************************************
	 *                                                                         *
	 * Private transient fields                                                *
	 *                                                                         *
	 **************************************************************************/

	transient private MediaSource source;
	transient private IObservableCollection<Tag> _tags;

	/***************************************************************************
	 *                                                                         *
	 * Constructors                                                            *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * ORM Constructor
	 */
	protected MediaItem() { }

	/**
	 * Creates a new MediaData item assigned to the given MediaLibrary and with
	 * the relative file path
	 *
	 * @param parentLibrary
	 * @param relativeFilePath
	 */
	protected MediaItem(MediaLibrary parentLibrary, URI relativeFilePath) {
		this(new MediaSource(parentLibrary, relativeFilePath));
	}

	/**
	 * Creates a new MediaData item assigned to the given MediaLibrary and with
	 * the relative file path
	 *
	 * @param mediaSource
	 */
	protected MediaItem(MediaSource mediaSource) {
        getSources().add(mediaSource);
		setFilename(NameUtil.prettifyName(mediaSource.getName()));
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Prototype this instance with the given one.
     * This will copy all values from the prototype and overwrite the values of this instance.
	 *
	 * @param prototype
	 */
	public void prototype(MediaItem prototype) {

		if(prototype == null) throw new IllegalArgumentException("prototype must not be NULL!");

        getSources().clear(); getSources().addAll(prototype.getSources());
        getTags().clear(); getTags().addAll(prototype.getTags());


        setFilehash(prototype.getFilehash());
        setFilename(prototype.getFilename());
        setAddedDate(prototype.getAddedDate());
        setFileSize(prototype.getFileSize());
        setResolution(prototype.getResolution());
        setOpened(prototype.getOpened());
		setRating(prototype.getRating());
        setType(prototype.getType());
        setLastAccessed(prototype.getLastAccessed());
    }


	/***************************************************************************
	 *                                                                         *
	 * Properties                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Gets the datetime when this media was added to the library
	 *
	 * @return
	 */
	public DateTime getAddedDate() {
		return addedDate;
	}

	/**
	 * Sets the datetime when this media was added to the library
	 *
	 * @param addedDate
	 */
	public void setAddedDate(DateTime addedDate) {
		this.addedDate = addedDate;
		firePropertyChange("addedDate");
	}

    /**
     * Gets the file size in bytes
     * @return
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the file size in bytes
     * @param fileSize
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
        firePropertyChange("fileSize");
    }

	/**
	 * Is the primary media source available
	 * @return
	 */
	@Transient
	public boolean isAvailable(){
		MediaSource source = getSource();
		return source != null && source.isAvailable();
	}

	/**
	 * Gets the best source for this media item.
	 * It will return one that is currently available - if possible.
	 * @return
	 */
	@Transient
	public MediaSource getSource(){
		if(source == null || !source.isAvailable())
		{
			Set<MediaSource> sources = getSources();
			if(sources != null)
				for (MediaSource s : sources) {
					if(s.isAvailable())
						source = s;
				}
			if(source != null && !sources.isEmpty())
				source = Lists.getFirst(sources);
		}
		return source;
	}

	/**
	 * Gets all known sources of this media item
	 *
	 * @return
	 */
	public Set<MediaSource> getSources() {
		return sources;
	}


	/**
	 * Gets all tags currently assigned to this media item
	 *
	 * @return
	 */
	public IObservableCollection<Tag> getTags() {
		if(_tags == null){
			_tags = new ObservableCollection<>(tags);
		}
		return _tags;
	}

	/**
	 * Checks if this MediaData has the given Tag assigned
	 *
	 * @param tag
	 * @return
	 */
	@Transient
	public boolean hasTag(Tag tag) {
		return getTags().contains(tag);
	}

	public MediaType getType() {
		return type;
	}

	public void setType(MediaType mediaType) {
		if(mediaType.equals(MediaType.UNKNOWN)) throw new IllegalArgumentException("mediaType must not be UNKNOWN!");
		this.type = mediaType;
		firePropertyChange("mediaType");
	}

	/**
	 * Gets how many times this media item was opened already
	 *
	 * @return
	 */
	public int getOpened() {
		return opened;
	}

	/**
	 * Set how many times this media item was opened
	 *
	 * @param opened
	 */
	public void setOpened(int opened) {
		this.opened = opened;
		firePropertyChange("opened");
	}


	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
		firePropertyChange("filename");
	}


	/**
	 * Returns a Hash from the file contents
	 * @return
	 */
	public String getFilehash() {
		return filehash;
	}

	/**
	 * Set the filehash of this media data.
	 * @param filehash
	 */
	public void setFilehash(String filehash) {
		this.filehash = filehash;
	}

	/**
	 * Get the image/video resolution of this media
	 * @return
	 */
	public Size getResolution() {
		return resolution;
	}

	/**
	 * Set the image/video resolution
	 * @param resolution
	 */
	public void setResolution(Size resolution) {
		this.resolution = resolution;
		firePropertyChange("resolution");
	}

	/**
	 * Has this media a resolution information?
	 * @return
	 */
	@Transient
	public boolean hasResolution(){ return resolution != null && !resolution.isEmpty();}


	/**
	 * Get the rating of this media. [0-5] stars
	 * @return
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * Set the rating of this media
	 * @param rating
	 */
	public void setRating(int rating) {
		this.rating = rating;
		firePropertyChange("rating");
	}

    public DateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(DateTime lastAccessed) {
		this.lastAccessed = lastAccessed;
		firePropertyChange("lastAccessed");
	}


	@Override
	public String toString() {
		return this.getFilename() + "hash: " + this.getFilehash() + " src: " + getSource();
	}

	@Override
	public abstract MediaItem clone();

}
