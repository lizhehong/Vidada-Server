package com.elderbyte.vidada.vidada.agents.local;

import com.elderbyte.vidada.vidada.agents.MediaAgent;
import com.elderbyte.vidada.vidada.agents.MediaMetadataDto;
import com.elderbyte.vidada.vidada.media.MediaItem;

/**
 * Reads extended file attributes to find meta-data
 */
public class LocalXattrMediaAgent implements MediaAgent {   // TODO

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
