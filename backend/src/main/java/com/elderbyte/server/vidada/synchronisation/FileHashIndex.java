package com.elderbyte.server.vidada.synchronisation;

import archimedes.core.concurrent.IProgressListener;
import archimedes.core.concurrent.ProgressEventArgs;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.server.vidada.media.MediaHashService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a index for file-hashes of a given set of files.
 */
class FileHashIndex {

    private final MediaHashService mediaHashService;
    private final List<ResourceLocation> mediaLocations;

    private Map<ResourceLocation, String> fileHashIndex = null;

    public FileHashIndex(MediaHashService mediaHashService, List<ResourceLocation> mediaLocations){
        this.mediaHashService = mediaHashService;
        this.mediaLocations = mediaLocations;
    }


    public void updateIndex(IProgressListener progressListener) {
        fileHashIndex = createIndex(progressListener);
    }

    public String getFileHash(ResourceLocation resource){
        return fileHashIndex.get(resource);
    }

    /**
     * Creates a Cache (Map) which holds the hash for each physical file.
     *
     * @param progressListener
     * @return
     */
    private Map<ResourceLocation, String> createIndex(IProgressListener progressListener){
        Map<ResourceLocation, String> fileContentMap = new HashMap<>();

        progressListener.currentProgress(new ProgressEventArgs(true, "Analyzing file contents..."));

        String hash;
        int locationCount = mediaLocations.size();
        for (int i = 0; i < locationCount; i++) {
            ResourceLocation location = mediaLocations.get(i);

            hash = mediaHashService.retrieveFileHash(location);
            fileContentMap.put(location, hash);
            progressListener.currentProgress(new ProgressEventArgs(100 / locationCount * i, "hash: " + hash + "\tfile: " + location.getName()));
        }

        return fileContentMap;
    }

}
