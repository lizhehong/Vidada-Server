package com.elderbyte.vidada.agents;

import com.elderbyte.vidada.media.MediaItem;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A media agent fetches additional meta-data for a media.
 *
 *
 */
public interface MediaAgent {

    /**
     * Fetches the meta-data for the given media from this agents source.
     * @param media
     * @return
     */
    MediaMetadataDto fetchMetadata(MediaItem media);


    /**
     * Can this media agent handle the given media
     * @param media
     * @return
     */
    boolean canHandle(MediaItem media);

}
