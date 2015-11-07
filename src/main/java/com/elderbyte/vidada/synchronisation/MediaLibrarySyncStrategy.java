package com.elderbyte.vidada.synchronisation;

import archimedes.core.concurrent.IProgressListener;
import archimedes.core.concurrent.ProgressEventArgs;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.util.Lists;
import com.elderbyte.vidada.media.MediaItem;
import com.elderbyte.vidada.media.libraries.MediaLibrary;
import com.elderbyte.vidada.media.source.MediaSource;
import com.elderbyte.vidada.tags.autoTag.AutoTagSupport;
import com.elderbyte.vidada.tags.autoTag.ITagGuessingStrategy;
import com.elderbyte.vidada.media.MediaHashService;
import com.elderbyte.vidada.media.MediaItemFactory;
import com.elderbyte.vidada.media.MediaService;
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
    private final ITagGuessingStrategy tagGuessingStrategy;

    private final Map<String, MediaItem> mediasInDatabase;

    public MediaLibrarySyncStrategy(MediaService mediaService, MediaHashService mediaHashService, ITagGuessingStrategy tagGuessingStrategy, Map<String, MediaItem> mediasInDatabase) {
        this.mediaService = mediaService;
        this.mediaHashService = mediaHashService;
        this.tagGuessingStrategy = tagGuessingStrategy;
        this.mediasInDatabase = mediasInDatabase;
    }

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
        mediaService.update( updateMedias );

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
                    // This media does no longer exist
                    if(canMediaBeDeleted( library, mediaInDatabase )){
                        removeMedias.add( mediaInDatabase );
                    }
                }
            }
        }

        return removeMedias;
    }


    /**
     * Remove the no longer existing media source
     * @param media
     */
    private boolean canMediaBeDeleted(MediaLibrary library, MediaItem media) {

        LOG.trace("handleNonExistingMedia: " + media );

        // Remove non existing file sources
        Set<MediaSource> allSources = media.getSources();
        for (MediaSource source : allSources) {
            if(source.getParentLibrary().equals(library))
            {
                media.getSources().remove(source); // TODO mark for UPDATE???
            }
        }
        // If the media has no sources left, we mark it as to be deleted
        return (media.getSources().size() == 0);
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

        // Update Tags from file path
        if(tagGuessingStrategy != null && AutoTagSupport.updateTags(tagGuessingStrategy, existingMedia)){
            hasChanges = true;
        }

        if(updateMediaProperties(existingMedia, parentLibrary)){
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

        for (MediaSource source : Lists.newList(existingMedia.getSources())) {

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

        List<MediaItem> newMedias = new ArrayList<MediaItem>(newfilesWithHash.size());

        double fileMapSize = newfilesWithHash.size();

        int i=0;
        for (Map.Entry<String, ResourceLocation> entry : newfilesWithHash.entrySet()) {

            int progress = (int)(100d / fileMapSize * (double)i);
            progressListener.currentProgress(new ProgressEventArgs(progress, "Importing new media:\t" + entry.getValue().getName()));
            MediaItem newMedia = MediaItemFactory.instance().buildMedia(entry.getValue(), parentlibrary, entry.getKey());

            if(newMedia != null)
            {
                // Add tags guessed from the file structure
                if(tagGuessingStrategy != null) {
                    AutoTagSupport.updateTags(tagGuessingStrategy, newMedia);
                }

                updateMediaProperties(newMedia, parentlibrary);
                newMedias.add(newMedia);
            }
            i++;
        }

        progressListener.currentProgress(new ProgressEventArgs(true, "Adding " + newMedias.size() + " new medias to the Library..."));
        mediaService.store(newMedias);
    }

    /**
     * Updates all necessary properties of this media
     * (file size, added date, resolution, duration etc)
     * if they are not yet present.
     *
     *
     * @param media
     * @return
     */
    private boolean updateMediaProperties(MediaItem media, MediaLibrary parentLibrary){

        boolean hasChanges = false;
        hasChanges = MediaItemFactory.instance().updateBasicAttributes(media);


        // TODO use Media-Metadata service

        return hasChanges;
    }


}
