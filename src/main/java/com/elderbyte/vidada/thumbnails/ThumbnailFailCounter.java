package com.elderbyte.vidada.thumbnails;

import com.elderbyte.vidada.media.MediaItem;

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
        Integer fails = failCounter.get(media.getFilehash()).get();
        return  (fails != null && fails > maxFailsAllowed);
    }

}
