package com.elderbyte.vidada.synchronisation;

import archimedes.core.concurrent.IProgressListener;
import archimedes.core.concurrent.ProgressEventArgs;
import com.elderbyte.vidada.agents.MetadataImportService;
import com.elderbyte.vidada.tasks.JobId;
import com.elderbyte.vidada.tasks.JobServiceProgressListener;
import com.elderbyte.vidada.tasks.JobState;
import com.elderbyte.vidada.tags.autoTag.ITagGuessingStrategy;
import com.elderbyte.vidada.tags.autoTag.KeywordBasedTagGuesser;
import com.elderbyte.vidada.tasks.JobService;
import com.elderbyte.vidada.media.libraries.MediaLibraryService;
import com.elderbyte.vidada.tags.TagService;
import com.elderbyte.vidada.media.MediaHashService;
import com.elderbyte.vidada.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

/**
 * Synchronizes a media folder with the vidada database.
 */
@Service
public class MediaSynchronisationService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JobService jobService;
    @Autowired
    private TagService tagService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private MediaHashService mediaHashService;
    @Autowired
    private MediaLibraryService mediaLibraryService;
    @Autowired
    private MetadataImportService metadataImportService;


	private final Object importLock = new Object();
    private volatile JobId currentImportJobId;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    public MediaSynchronisationService(){

	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    /**
     * Synchronizes all media libraries with the actual content on the storage.
     * If there is already a synchronisation going on, this call wont start a new one.
     * @param userListener
     * @return Returns a Job which represents the ongoing synchronisation
     */
    public JobId synchronizeAll(final IProgressListener userListener) {
        synchronized ( importLock ) {

            if(currentImportJobId == null){

                logger.info("Starting new library synchronisation...");

                currentImportJobId = jobService.create("MediaLibrary Importer");

                final IProgressListener progressListener = createJobProgressWrapper(userListener);

                synchronizeAllAsync(progressListener)
                    .thenRun(() -> {
                        // Synchronisation completed successful
                        currentImportJobId = null;
                        progressListener.currentProgress(ProgressEventArgs.COMPLETED);
                    })
                    .exceptionally(e -> {
                        // Synchronisation failed!

                        logger.error("Synchronisation failed!", e);
                        progressListener.currentProgress(ProgressEventArgs.FAILED);
                        currentImportJobId = null;
                        return null;
                    });

                jobService.notifyState(currentImportJobId, JobState.Running);

                return currentImportJobId;

            }else {
                logger.info("Import is already running, returning current job.");
                return currentImportJobId;
            }
        }
    }

    /**
     * Synchronizes all media libraries with the actual content on the storage
     * @return
     */
	public JobId synchronizeAll() {
        return synchronizeAll(null);
	}


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Synchronize all media libraries async
     * @param progressListener
     * @return Returns a task which represents the ongoing operation
     */
    @Transactional
    protected CompletableFuture<?> synchronizeAllAsync(final IProgressListener progressListener){

        // Ensure that tag-relations are reloaded
        tagService.invalidateTagRelations();

        logger.info("Starting to synchronize all media libraries...");

        final MediaImportStrategy importStrategy = createImportStrategy();

        return importStrategy.synchronizeAsync(progressListener)
            .thenRun(() -> {
                metadataImportService.updateAllMediaMetadataAsync();
        });
    }


    private MediaImportStrategy createImportStrategy(){
        return new MediaImportStrategy(
                mediaService,
                mediaHashService,
                mediaLibraryService.getAllLibraries());
    }

    private IProgressListener createJobProgressWrapper(IProgressListener listener){
        return  new JobServiceProgressListener(jobService, currentImportJobId) {
            public void currentProgress(ProgressEventArgs progressInfo) {
                super.currentProgress(progressInfo);
                if (listener != null) {
                    listener.currentProgress(progressInfo);
                }
                logger.debug(progressInfo.getCurrentTask());
            }
        };
    }

    public boolean isBusy() {
        return currentImportJobId != null;
    }
}
