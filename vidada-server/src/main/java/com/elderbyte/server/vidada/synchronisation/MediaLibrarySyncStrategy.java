package com.elderbyte.server.vidada.synchronisation;


import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.server.vidada.media.MediaHashService;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.MediaItemFactory;
import com.elderbyte.server.vidada.media.MediaService;
import com.elderbyte.server.vidada.media.libraries.MediaLibrary;
import com.elderbyte.server.vidada.media.source.MediaSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

/**
 * A strategy which can synchronize a media folder with the vidada database.
 */
class MediaLibrarySyncStrategy {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final MediaService mediaService;
    private final MediaHashService mediaHashService;

    private final Map<String, MediaItem> mediasInDatabase;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a sync strategy which can synchronize media libraries
     * @param mediaService
     * @param mediaHashService
     * @param mediasInDatabase
     */
    public MediaLibrarySyncStrategy(MediaService mediaService, MediaHashService mediaHashService, Map<String, MediaItem> mediasInDatabase) {
        this.mediaService = mediaService;
        this.mediaHashService = mediaHashService;
        this.mediasInDatabase = mediasInDatabase;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Synchronizes the given media library
     * @param progressListener
     * @param library
     */
    public void synchronize(IProgressListener progressListener, MediaLibrary library){
        progressListener.currentProgress(new ProgressEventArgs(true, "Scanning for media files in " + library + " ..."));

        List<ResourceLocation> mediaLocations = library.getMediaDirectory().getAllMediaFilesRecursive();


        if(!mediaLocations.isEmpty()) {
            progressListener.currentProgress(new ProgressEventArgs(true, "Media files found:\t " + mediaLocations.size()));

            //
            // now lets check against existing medias in our database
            //
            Map<String, ResourceLocation> newFiles = compareWithExisting(progressListener, mediaLocations, library);

            // Import the new found files
            progressListener.currentProgress(new ProgressEventArgs(true, "Starting import of new files..."));
            importNewFiles(progressListener, library, newFiles);

        }else{
            progressListener.currentProgress(new ProgressEventArgs(true, "No media files found in library " + library));
        }
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    /**
     * Compare the current existing files with the ones already indexed inside the database.
     *
     * @param progressListener
     * @return returns the found new (not indexed) files
     */
    private Map<String, ResourceLocation> compareWithExisting(IProgressListener progressListener, List<ResourceLocation> physicalExistingResources, MediaLibrary library){

        // Create a file hash index for all physical files
        progressListener.currentProgress(new ProgressEventArgs(true, "Building file hash index..."));
        FileHashIndex fileHashIndex = new FileHashIndex(mediaHashService, physicalExistingResources);
        fileHashIndex.updateIndex(progressListener);


        progressListener.currentProgress(new ProgressEventArgs(true, "Comparing with current db..."));

        Map<String, ResourceLocation> newFiles = new HashMap<>();
        Set<MediaItem> updateMedias = new HashSet<>();
        Set<MediaItem> stillExisting = new HashSet<>();

        progressListener.currentProgress(new ProgressEventArgs(true, "Compare the " + physicalExistingResources.size() + " physical existing files with the current index media items"));

        int i = 0;
        double fileMapSize = physicalExistingResources.size();
        for (ResourceLocation resource : physicalExistingResources) {

            String resourceHash = fileHashIndex.getFileHash(resource);

            if(mediasInDatabase.containsKey(resourceHash))
            {
                MediaItem existingMedia = mediasInDatabase.get(resourceHash);
                stillExisting.add(existingMedia); // mark the media data as real existing

                //
                // this file hash was already present in our media lib.
                // check if the details are still the same and update it if necessary
                //
                if(updateExistingMedia(existingMedia, library, resource)) {
                    updateMedias.add(existingMedia);
                }

            }else{
                newFiles.put(resourceHash, resource);
            }

            int progress = (int)(100d / fileMapSize * (double)i);
            progressListener.currentProgress(new ProgressEventArgs(progress, "Importing:\t" + resource.getName()));
            i++;
        }

        progressListener.currentProgress(new ProgressEventArgs(true, "Checking " + mediasInDatabase.size() + " medias..."));

        Set<MediaItem> removedMedias = findRemovedMedias( library, mediasInDatabase.values(), stillExisting );

        // bulk update
        mediaService.delete( removedMedias );
        mediaService.save( updateMedias );

        return newFiles;
    }

    /**
     * Collect the no longer existing media files in the current media library
     *
     * @param library  The media library
     * @param mediasInDatabase All medias in the database
     * @param stillExisting A set of physically existing medias
     * @return
     */
    private Set<MediaItem> findRemovedMedias(MediaLibrary library, Collection<MediaItem>  mediasInDatabase, Set<MediaItem> stillExisting){

        Set<MediaItem> removeMedias = new HashSet<>();

        for (MediaItem mediaInDatabase : mediasInDatabase) {
            // check if the parent library is still correct
            if(isMemberOfLibrary(mediaInDatabase, library))
            {
                boolean isStillExisting = stillExisting.contains(mediaInDatabase);
                if(!isStillExisting){
                    // This media does no longer exist in the current library
                    // Thus we can remove all sources from this library of this media
                    removLibrarySources(mediaInDatabase, library);

                    if(hasAnySource(mediaInDatabase)){
                        // The media has no source anymore (should no longer show up)
                        removeMedias.add( mediaInDatabase );
                    }
                }
            }
        }

        return removeMedias;
    }


    /**
     * Check if the media has any media-source left
     * @param media
     */
    private boolean hasAnySource(MediaItem media) {
        return (media.getSources().size() == 0);
    }

    /**
     * Remove all media-sources which are from the given library.
     * @param media
     * @param library
     */
    private void removLibrarySources(MediaItem media, MediaLibrary library){

        // Remove non existing file sources
        Set<MediaSource> allSources = new HashSet<>(media.getSources());
        for (MediaSource source : allSources) {
            if (source.getParentLibrary().equals(library)) {
                media.getSources().remove(source);
            }
        }
    }

    /**
     * Is this media a member of the given library?
     *
     * @param library
     * @return
     */
    private boolean isMemberOfLibrary(MediaItem media, MediaLibrary library) {
        if(library == null) throw new IllegalArgumentException("library must not be NULL!");

        for (MediaSource source : media.getSources()) {
            if(source != null){
                MediaLibrary parentLib = source.getParentLibrary();
                if(parentLib != null){
                    return parentLib.equals(library);
                }else{
                    LOG.error("Parent library of " + source + " was NULL!");
                }
            }else{
                LOG.error("MediaSource of media " + media + " was NULL!");
            }
        }
        return false;
    }


    /**
     * Update a existing media item
     * @param existingMedia
     * @param parentLibrary
     * @param resource
     * @return Returns true if any property of this media has been updated
     */
    private boolean updateExistingMedia(MediaItem existingMedia, MediaLibrary parentLibrary, ResourceLocation resource){

        boolean hasChanges = updateExistingMediaSources(parentLibrary, existingMedia, resource);

        if(updateBasicMediaMetadata(existingMedia, parentLibrary)){
            hasChanges = true;
        }

        if(hasChanges)LOG.info(existingMedia + "has changes!");

        return hasChanges;
    }

    /**
     * Update the media sources of the given media library
     * (That means that current paths will be updated)
     * @param library
     * @param existingMedia
     * @param currentPath
     * @return
     */
    private boolean updateExistingMediaSources(MediaLibrary library, MediaItem existingMedia, ResourceLocation currentPath){
        boolean hasChanges = false;

        boolean currentPathExists = false;

        for (MediaSource source : new ArrayList<>(existingMedia.getSources())) {

            if(source.getParentLibrary() == null){
                existingMedia.getSources().remove(source);
                hasChanges = true;
            }else if(source.getParentLibrary().equals(library))
            {
                if(!source.isAvailable()){
                    LOG.debug("Removing old source: " + source);
                    existingMedia.getSources().remove(source);
                    hasChanges = true;
                }else{
                    if(source.getRelativePath().equals(library.getMediaDirectory().getRelativePath(currentPath)))
                    {
                        currentPathExists = true;
                    }
                }
            }
        }

        // if the media has not yet this library as source, try to add it as one
        if(!currentPathExists)
        {
            URI relativePath = library.getMediaDirectory().getRelativePath(currentPath);
            LOG.debug("trying to add new source: " + relativePath);
            if(relativePath != null){
                MediaSource source = new MediaSource(library, relativePath);
                existingMedia.getSources().add(source);
                hasChanges = true;
            }
        }

        return hasChanges;
    }



    /**
     * Import the new found media files
     * @param progressListener
     * @param newfilesWithHash New file tuples, with pre-calculated file content hashes
     */
    private void importNewFiles(IProgressListener progressListener, MediaLibrary parentlibrary, Map<String, ResourceLocation> newfilesWithHash){
        progressListener.currentProgress(new ProgressEventArgs(true, "Importing " + newfilesWithHash.size() + " new files..."));

        List<MediaItem> newMedias = new ArrayList<>(newfilesWithHash.size());

        double fileMapSize = newfilesWithHash.size();

        int i=0;
        for (Map.Entry<String, ResourceLocation> entry : newfilesWithHash.entrySet()) {

            int progress = (int)(100d / fileMapSize * (double)i);
            progressListener.currentProgress(new ProgressEventArgs(progress, "Importing new media:\t" + entry.getValue().getName()));
            MediaItem newMedia = MediaItemFactory.instance().buildMedia(entry.getValue(), parentlibrary, entry.getKey());

            if(newMedia != null)
            {
                updateBasicMediaMetadata(newMedia, parentlibrary);
                newMedias.add(newMedia);
            }
            i++;
        }

        progressListener.currentProgress(new ProgressEventArgs(true, "Adding " + newMedias.size() + " new medias to the Library..."));
        mediaService.save(newMedias);
    }

    /**
     * Updates all necessary properties of this media
     * (file size, added date, resolution, duration etc)
     * if they are not yet present.
     *
     * @param media
     * @return
     */
    private boolean updateBasicMediaMetadata(MediaItem media, MediaLibrary parentLibrary){
        return MediaItemFactory.instance().updateBasicAttributes(media);
    }


}
