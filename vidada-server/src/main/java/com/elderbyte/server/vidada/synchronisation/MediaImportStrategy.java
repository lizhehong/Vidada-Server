package com.elderbyte.server.vidada.synchronisation;

import com.elderbyte.server.vidada.media.MediaHashService;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.MediaService;
import com.elderbyte.server.vidada.media.libraries.MediaLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This class implements basic media import functionality
 * to import MediaLibraries.
 *
 * You should not cache instances of this class, instead
 * create a new instance for each Import for most accurate results.
 * (This class caches a lot of environment states when being created)
 *
 * @author IsNull
 *
 */
class MediaImportStrategy {

    /***************************************************************************
     *                                                                         *
     * Private final fields                                                    *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MediaService mediaService;
    private final MediaHashService mediaHashService;
    private final List<MediaLibrary> libraries = new ArrayList<>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaImportStrategy to synchronize the specified libraries.
     *
     * @param mediaService
     * @param libraries
     */
	public MediaImportStrategy(MediaService mediaService, MediaHashService mediaHashService, List<MediaLibrary> libraries){
		this.mediaService = mediaService;
        this.mediaHashService = mediaHashService;

		this.libraries.addAll(libraries);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**{@inheritDoc}*/
	public CompletableFuture<?> synchronizeAsync(IProgressListener progressListener){
        return CompletableFuture.runAsync(() -> {
            // Init
            Map<String, MediaItem> allMediasInDatabase = fetchAllMediasInDatabase();

            progressListener.currentProgress(new ProgressEventArgs(true, "Searching for all media files in your libraries (" + libraries.size() + ") ..."));

            if (!libraries.isEmpty()) {
                for (MediaLibrary lib : libraries) {
                    if (lib.isAvailable()) {
                        synchronizeLibrary(progressListener, lib, allMediasInDatabase);
                    } else {
                        logger.warn("Library root folder is not available (does not exist): " + lib.getLibraryRoot());
                    }
                }

                progressListener.currentProgress(new ProgressEventArgs(100, "Done."));
            } else {
                logger.info("Import aborted, you do not have specified any libraries!");
            }
        });
	}


	/***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

	private void synchronizeLibrary(IProgressListener progressListener, MediaLibrary lib, Map<String, MediaItem> allMediasInDatabase) {
        MediaLibrarySyncStrategy librarySyncStrategy = new MediaLibrarySyncStrategy(mediaService, mediaHashService, allMediasInDatabase);
        librarySyncStrategy.synchronize(progressListener, lib);
	}

    /**
     * Returns all medias which are currently in the database
     * @return
     */
	private Map<String, MediaItem> fetchAllMediasInDatabase(){

		Map<String, MediaItem> existingMediaData = new HashMap<>();

		List<MediaItem> knownMedias = mediaService.findAllMedias();

		for (MediaItem mediaData : knownMedias) {
			existingMediaData.put(mediaData.getFilehash(), mediaData);
		}
		return existingMediaData;
	}


}
