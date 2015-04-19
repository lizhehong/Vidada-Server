package com.elderbyte.vidada.domain.jobs;


import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.concurrent.IProgressListener;
import archimedes.core.concurrent.ProgressEventArgs;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class JobServiceProgressListener implements IProgressListener {

    /*
	private static final Logger logger = LogManager.getLogger(JobServiceProgressListener.class.getName());


	private final JobId jobId;
	private final IJobService jobService;


	public JobServiceProgressListener(IJobService jobService, JobId jobId){
		this.jobId = jobId;
		this.jobService = jobService;
	}


	@Override
	public void currentProgress(ProgressEventArgs progressInfo) {

		//logger.info( progressInfo.getProgressInPercent() + "% - " + progressInfo.getCurrentTask());

		if(progressInfo.isRunning()){
			jobService.notifyProgress(
                    jobId,
                    progressInfo.getCurrentTask(),
                    progressInfo.getProgressInPercent());
		}else {
			if(progressInfo.hasFailed()){
				jobService.notifyState(jobId, JobState.Failed);
			}else if(progressInfo.isCompleted()){
				jobService.notifyState(jobId, JobState.Completed);
			}else {
				throw new NotSupportedException();
			}
		}
	}
	*/

    @Override
    public void currentProgress(ProgressEventArgs progressInfo) {
        throw new NotImplementedException(); // TODO
    }

}
