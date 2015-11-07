package com.elderbyte.vidada.jobs;

/**
 * Represents the state of a job.
 */
public enum JobState {

    /**
     *
     */
    Idle,

    /**
     * The job is currently running.
     */
	Running,

    /**
     * The job has completed success fully
     */
	Completed,

    /**
     * The job has quit with a fail.
     */
	Failed

}
