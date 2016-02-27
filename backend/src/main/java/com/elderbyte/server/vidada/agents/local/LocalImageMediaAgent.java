package com.elderbyte.server.vidada.agents.local;

import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.server.vidada.agents.MediaAgent;
import com.elderbyte.server.vidada.agents.MediaMetadataDto;
import com.elderbyte.server.vidada.images.ImageUtil;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.MediaType;
import com.elderbyte.server.vidada.media.Resolution;
import com.elderbyte.server.vidada.media.source.MediaSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Uses local data to fetch meta-data for images.
 */
public class LocalImageMediaAgent implements MediaAgent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public MediaMetadataDto fetchMetadata(MediaItem media) {

        MediaMetadataDto metaData = new MediaMetadataDto();

        MediaSource source = media.getSource();

        metaData.setResolution(getResolution(source));

        return metaData;
    }

    @Override
    public boolean canHandle(MediaItem media) {
        return media.getType() == MediaType.IMAGE;
    }

    @Override
    public void refresh() {

    }


    private Resolution getResolution(MediaSource source){
        try{
            ResourceLocation imageResource = source.getResourceLocation();
            if(imageResource != null && imageResource.exists()){
                return ImageUtil.getImageResolution(imageResource);
            }
        }catch(IOException e){
            logger.error("Failed to fetch image resolution!", e);
        }
        return null;
    }
}
