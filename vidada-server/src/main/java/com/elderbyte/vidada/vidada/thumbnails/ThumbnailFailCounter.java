package com.elderbyte.vidada.vidada.thumbnails;

import com.elderbyte.vidada.vidada.media.MediaItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Keeps track of how many times the creation of a thumbnail has failed.
 * This class is thread safe.
 */
public class ThumbnailFailCounter {


    private final Map<String, AtomicInteger> failCounter = Collections.synchronizedMap(new HashMap<>());
    private final int maxFailsAllowed;


    /**
     * Creates a new ThumbnailFailCounter
     * @param maxFailsAllowed
     */
    public ThumbnailFailCounter(int maxFailsAllowed){
        this.maxFailsAllowed = maxFailsAllowed;
    }


    public void onThumbCreationSuccess(MediaItem media){
        failCounter.remove(media.getFilehash());
    }

    /**
     * Inform this fail counter that the given item has failed.
     * @param media
     */
    public synchronized void onThumbCreationFailed(MediaItem media){
        AtomicInteger fails = failCounter.get(media.getFilehash());
        if(fails == null){
            fails = new AtomicInteger();
            failCounter.put(media.getFilehash(), fails);
        }
        fails.incrementAndGet();
    }

    /**
     *
     * @param media
     * @return
     */
    public boolean hasFailedTooOften(MediaItem media){
        AtomicInteger fails = failCounter.get(media.getFilehash());
        return  (fails != null && fails.get() > maxFailsAllowed);
    }

}
