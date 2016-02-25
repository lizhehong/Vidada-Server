package com.elderbyte.server.vidada.agents.tmdb;

import com.elderbyte.server.vidada.agents.MediaAgent;
import com.elderbyte.server.vidada.agents.MediaMetadataDto;
import com.elderbyte.server.vidada.media.MediaItem;

/**
 *
 */
public class TmdbMediaAgent implements MediaAgent {

    @Override
    public MediaMetadataDto fetchMetadata(MediaItem media) {
        return null;
    }

    @Override
    public boolean canHandle(MediaItem media) {
        return false;
    }

    @Override
    public void refresh() {

    }
}
