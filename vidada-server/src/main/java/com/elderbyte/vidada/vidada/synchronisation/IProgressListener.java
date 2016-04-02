package com.elderbyte.vidada.vidada.synchronisation;

/**
 * A progress listener reports periodically the current status of an async running operation.
 *
 * @author IsNull
 *
 */
public interface IProgressListener {
    /**
	 * Called whenever a new status has been reached.
	 * @param progressInfo The ProgressEventArgs provide basic information about the current task.
	 */
	abstract void currentProgress(ProgressEventArgs progressInfo);
}
