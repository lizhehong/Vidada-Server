package com.elderbyte.server.vidada.thumbnails;

import com.elderbyte.server.vidada.media.MediaItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class ThumbnailFailCounter {


    private final Map<String, AtomicInteger> failCounter = Collections.synchronizedMap(new HashMap<>());
    private final int maxFailsAllowed;


    public ThumbnailFailCounter(int maxFailsAllowed){
        this.maxFailsAllowed = maxFailsAllowed;
    }


    public void onThumbCreationSuccess(MediaItem media){
        failCounter.remove(media.getFilehash());
    }

    public synchronized void onThumbCreationFailed(MediaItem media){
        AtomicInteger fails = failCounter.get(media.getFilehash());
        if(fails == null){
            fails = new AtomicInteger();
            failCounter.put(media.getFilehash(), fails);
        }
        fails.incrementAndGet();
    }

    public boolean hasFailedTooOften(MediaItem media){
        AtomicInteger fails = failCounter.get(media.getFilehash());
        return  (fails != null && fails.get() > maxFailsAllowed);
    }

}
