package com.elderbyte.vidada.service.sync;

import archimedes.core.concurrent.IProgressListener;
import archimedes.core.concurrent.ProgressEventArgs;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.domain.tags.autoTag.ITagGuessingStrategy;
import com.elderbyte.vidada.service.media.MediaHashService;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final ITagGuessingStrategy tagGuessingStrategy;
    private final List<MediaLibrary> libraries = new ArrayList<MediaLibrary>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaImportStrategy to synchronize the specified libraries.
     *
     * @param mediaService
     * @param tagGuessingStrategy
     * @param libraries
     */
	public MediaImportStrategy(MediaService mediaService, MediaHashService mediaHashService, ITagGuessingStrategy tagGuessingStrategy, List<MediaLibrary> libraries){
		this.mediaService = mediaService;
        this.mediaHashService = mediaHashService;
        this.tagGuessingStrategy = tagGuessingStrategy;

		this.libraries.addAll(libraries);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**{@inheritDoc}*/
	public void synchronize(IProgressListener progressListener){
        try {
            // Init
            Map<String, MediaItem> allMediasInDatabase = fetchAllMediasInDatabase();

            progressListener.currentProgress(new ProgressEventArgs(true, "Searching for all media files in your libraries (" + libraries.size() + ") ..."));

            if (!libraries.isEmpty()) {
                for (MediaLibrary lib : libraries) {
                    if (lib.isAvailable()) {
                        synchronizeLibrary(progressListener, lib, allMediasInDatabase);
                    }else{
                        logger.warn("Library root folder is not available (does not exist): " + lib.getLibraryRoot());
                    }
                }

                progressListener.currentProgress(new ProgressEventArgs(100, "Done."));
            } else {
                logger.info("Import aborted, you do not have specified any libraries!");
            }
        }finally {
            progressListener.currentProgress(ProgressEventArgs.COMPLETED);
        }
	}


	/***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

	private void synchronizeLibrary(IProgressListener progressListener, MediaLibrary lib, Map<String, MediaItem> allMediasInDatabase) {
		MediaLibrarySyncStrategy librarySyncStrategy = new MediaLibrarySyncStrategy(mediaService, mediaHashService, tagGuessingStrategy, allMediasInDatabase);
		librarySyncStrategy.synchronize(progressListener, lib);
	}

    /**
     * Returns all medias which are currently in the database
     * @return
     */
	private Map<String, MediaItem> fetchAllMediasInDatabase(){

		Map<String, MediaItem> existingMediaData = new HashMap<String, MediaItem>();

		List<MediaItem> knownMedias = mediaService.getAllMedias();

		for (MediaItem mediaData : knownMedias) {
			existingMediaData.put(mediaData.getFilehash(), mediaData);
		}
		return existingMediaData;
	}


}
