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
import com.elderbyte.vidada.service.TagService;
import com.elderbyte.vidada.service.media.MediaHashService;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

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
    private Thread importThread = null;
	private volatile JobId currentImportJobId;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    @Inject
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



    public JobId synchronizeAll(final IProgressListener userListener) {
        synchronized ( importLock ) {

            if(currentImportJobId == null){

                currentImportJobId = jobService.create("MediaLibrary Importer");

                importThread = new Thread(() -> synchronizeAllSync(userListener));
                importThread.start();

                jobService.notifyState(currentImportJobId, JobState.Running);

                return currentImportJobId;

            }else {
                logger.info("Import is already running, returning current job.");
                return currentImportJobId;
            }
        }
    }

	public JobId synchronizeAll() {
        return synchronizeAll(null);
	}


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    @Transactional
    protected void synchronizeAllSync(final IProgressListener userListener){
        final MediaImportStrategy importStrategy = createImportStrategy();

        final IProgressListener progressListener = new JobServiceProgressListener(jobService, currentImportJobId) {
            public void currentProgress(ProgressEventArgs progressInfo) {
                super.currentProgress(progressInfo);
                if (userListener != null) {
                    userListener.currentProgress(progressInfo);
                }
            }
        };

        try {
            importStrategy.synchronize(progressListener);
            progressListener.currentProgress(ProgressEventArgs.COMPLETED);
        } catch (Exception e) {
            logger.error("Media synchronisation failed.", e);
            progressListener.currentProgress(ProgressEventArgs.FAILED);
        } finally {
            currentImportJobId = null;
        }
    }


    private MediaImportStrategy createImportStrategy(){
        ITagGuessingStrategy strategy = new KeywordBasedTagGuesser(tagService.getAllTags());

        MediaImportStrategy importStrategy = new MediaImportStrategy(
                mediaService,
                mediaHashService,
                strategy,
                mediaLibraryService.getAllLibraries());

        return importStrategy;
    }

}
