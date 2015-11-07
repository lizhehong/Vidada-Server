package com.elderbyte.vidada.jobs;

import java.util.ArrayList;
import java.util.List;

public class JobInfo implements Cloneable {

    private final int id;
    private final String name;
    private final List<String> subTasks = new ArrayList<String>();
	private JobState state = JobState.Idle;
	private float progress = 0;

    /**
     * Creates a new Job
     * @param id
     * @param name
     * @param state
     */
	public JobInfo(int id, String name, JobState state){
        this.id = id;
        this.name = name;
		this.state = state;
	}

    /**
     * Prototype constructor
     * @param prototype
     */
    protected JobInfo(JobInfo prototype){
        this.id = prototype.getId();
        this.name = prototype.getName();
        this.subTasks.addAll(prototype.getSubTasks());
        this.state = prototype.getState();
        this.progress = prototype.getProgress();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public List<String> getSubTasks() {
        return subTasks;
    }

    @Override
    public JobInfo clone(){
        return new JobInfo(this);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JobId other = (JobId) obj;
        if (id != other.getId())
            return false;
        return true;
    }

}
