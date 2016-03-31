package com.elderbyte.server.vidada.synchronisation;

/**
 * Information about the current progress
 * @author IsNull
 *
 */
public class ProgressEventArgs {

	/**
     * Represents completion event
	 */
	public static final ProgressEventArgs COMPLETED = new ProgressEventArgs(true, false);

	/**
	 * Represents failed event (means that the task is done, completed is not set however)
	 */
	public static final ProgressEventArgs FAILED = new ProgressEventArgs(false, true);

	private final boolean completed;
	private final boolean failed;


	private final float progressInPercent;
	private final String currentTask;
	private final boolean indeterminant;

	public ProgressEventArgs(float progressInPercent, String currentTask){
		this.progressInPercent = progressInPercent;
		this.currentTask = currentTask;
		this.indeterminant = false;
		this.completed = false;
		this.failed = false;
	}

	public ProgressEventArgs(boolean indeterminant, String currentTask){
		this.indeterminant = indeterminant;
		this.currentTask = currentTask;
		this.progressInPercent = 0;
		this.completed = false;
		this.failed = false;
	}

	private ProgressEventArgs(boolean completed, boolean failed){
		this.completed = completed;
		this.failed = failed;

		this.indeterminant = false;
		this.currentTask = "";
		this.progressInPercent = 0;
	}


	public synchronized float getProgressInPercent() {
		return progressInPercent;
	}

	public synchronized String getCurrentTask() {
		return currentTask;
	}

	public synchronized boolean isIndeterminant() {
		return indeterminant;
	}

	@Override
	public String toString(){
		return getCurrentTask() + " " + getProgressInPercent() + "%";
	}

	public boolean hasFailed() {
		return failed;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isRunning(){
		return !isCompleted() && !hasFailed();
	}
}
