package com.elderbyte.vidada.service.media;

import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.domain.media.*;
import com.elderbyte.vidada.domain.media.source.MediaSource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class MediaItemFactory {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final static MediaItemFactory instance = new MediaItemFactory();


    /***************************************************************************
     *                                                                         *
     * Singleton                                                               *
     *                                                                         *
     **************************************************************************/

    public synchronized static MediaItemFactory instance(){
        return instance;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


	/**
     * Simple media factory to create a media item from an existing file.
	 * The media type is determined dynamically.
	 * @param mediaLocation
	 * @param parentLibrary
	 * @param mediaHash
	 * @return
	 */
	public MediaItem buildMedia(final ResourceLocation mediaLocation, final MediaLibrary parentLibrary, String mediaHash) {

        if(mediaHash == null) throw new IllegalArgumentException("mediaHash must not be null!");

		MediaItem newMedia = null;

        MediaSource source = new MediaSource(parentLibrary, parentLibrary.getMediaDirectory().getRelativePath(mediaLocation));

        switch (source.findMediaType()){
            case MOVIE:
                newMedia = new MovieMediaItem( source, mediaHash);
                break;

            case IMAGE:
                newMedia = new ImageMediaItem( source, mediaHash);
                break;

            default:
                LOG.error("Can not build media for " + mediaLocation.toString());
                break;
        }

        return newMedia;
	}

    /**
     * Updates the basic attributes of the given media item
     * @param mediaItem
     * @return
     */
    public boolean updateBasicAttributes(MediaItem mediaItem){

        LOG.debug("Updating media " + mediaItem);

        boolean wasUpdated = false;

        if(mediaItem.getFileSize() <= 0){ // Checks if a update is required

            MediaSource source = mediaItem.getSource();
            ResourceLocation mediaLocation = source.getResourceLocation();

            Path mediaFile = new File(mediaLocation.getPath()).toPath();
            try {
                BasicFileAttributes attr = Files.readAttributes(mediaFile, BasicFileAttributes.class);
                FileTime time = attr.creationTime();
                long fileSize = attr.size();

                mediaItem.setAddedDate(new DateTime(time.toMillis()));
                mediaItem.setFileSize(fileSize);
                wasUpdated = true;
            } catch (IOException e) {
                LOG.error("Failed to update basic attributes!", e);
            }
        }

        return wasUpdated;
    }

}
