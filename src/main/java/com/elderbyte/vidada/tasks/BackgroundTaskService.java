package com.elderbyte.vidada.tasks;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;


@Service
public class BackgroundTaskService {

    private int maxParallelTasks = 5;

    private final ForkJoinPool mainPool;


    public BackgroundTaskService(){
        mainPool = new ForkJoinPool(maxParallelTasks);
    }

    public <T> CompletableFuture<T> submitTask(Supplier<T> task){
        return CompletableFuture.supplyAsync(task, mainPool);
    }

    public int getQueuedTaskCount(){
        return mainPool.getQueuedSubmissionCount();
    }

}
