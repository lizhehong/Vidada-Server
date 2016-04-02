package com.elderbyte.vidada.vidada.tasks;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@Service
public class JobService {

    // TODO: We keep all jobs in this map (actually a memory leak)
    private final Map<JobId, JobInfo> jobs = new HashMap<>();
    private AtomicInteger idPool = new AtomicInteger(0);


    public JobService() {

    }

    public JobId create(String name) {
        JobId jobId = nextJobId();
        JobInfo job = new JobInfo(jobId.getId(), name, JobState.Idle);
        jobs.put(jobId, job);
        return jobId;
    }

    public JobInfo pollProgress(JobId jobId) {
        JobInfo job = jobs.get(jobId);
        if(job != null){
            job = job.clone();
        }
        return job;
    }

    public void notifyProgress(JobId jobId, String currentTask, float progress) {
        JobInfo job = jobs.get(jobId);
        if(job != null){
            job.getSubTasks().add(currentTask);
            job.setProgress(progress);
        }
    }

    public void notifyState(JobId jobId, JobState state) {
        JobInfo job = jobs.get(jobId);
        if(job != null){
            job.setState(state);
        }
    }

    private JobId nextJobId(){
        return new JobId(idPool.incrementAndGet());
    }
}
