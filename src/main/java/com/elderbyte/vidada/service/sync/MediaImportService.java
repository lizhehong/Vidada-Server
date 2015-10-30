package com.elderbyte.vidada.service.sync;

import archimedes.core.concurrent.IProgressListener;
import archimedes.core.concurrent.ProgressEventArgs;
import com.elderbyte.vidada.domain.jobs.JobId;
import com.elderbyte.vidada.service.JobServiceProgressListener;
import com.elderbyte.vidada.domain.jobs.JobState;
import com.elderbyte.vidada.domain.tags.autoTag.ITagGuessingStrategy;
import com.elderbyte.vidada.domain.tags.autoTag.KeywordBasedTagGuesser;
import com.elderbyte.vidada.service.JobService;
import com.elderbyte.vidada.service.MediaLibraryService;
import com.elderbyte.vidada.service.tags.TagService;
import com.elderbyte.vidada.service.media.MediaHashService;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class MediaImportService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JobService jobService;
    private final TagService tagService;
    private final MediaService mediaService;
    private final MediaHashService mediaHashService;
    private final MediaLibraryService mediaLibraryService;


	private final Object importLock = new Object();
    private volatile JobId currentImportJobId;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    @Autowired
    public MediaImportService(
            JobService jobService,
            TagService tagService,
            MediaService mediaService,
            MediaHashService mediaHashService,
            MediaLibraryService mediaLibraryService) {
        this.jobService = jobService;
        this.tagService = tagService;
        this.mediaService = mediaService;
        this.mediaHashService = mediaHashService;
        this.mediaLibraryService = mediaLibraryService;
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

        return importStrategy.synchronizeAsync(progressListener);
    }


    private MediaImportStrategy createImportStrategy(){
        ITagGuessingStrategy strategy = new KeywordBasedTagGuesser(tagService.getAllTags());

        return new MediaImportStrategy(
                mediaService,
                mediaHashService,
                strategy,
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
