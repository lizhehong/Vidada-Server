package com.elderbyte.vidada.agents.local;

import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.ffmpeg.VideoInfo;
import com.elderbyte.vidada.agents.MediaAgent;
import com.elderbyte.vidada.agents.MediaMetadataDto;
import com.elderbyte.vidada.media.MediaItem;
import com.elderbyte.vidada.media.MediaType;
import com.elderbyte.vidada.media.source.MediaSource;
import com.elderbyte.vidada.video.IVideoAccessService;
import com.elderbyte.vidada.video.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Uses local data to fetch meta-data for videos such as resolution, bitrate, duration.
 */
public class LocalVideoMediaAgent  implements MediaAgent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IVideoAccessService videoAccessService;

    public LocalVideoMediaAgent(IVideoAccessService videoAccessService){
        if(videoAccessService == null) throw new ArgumentNullException("videoAccessService");
        this.videoAccessService = videoAccessService;
    }

    @Override
    public MediaMetadataDto fetchMetadata(MediaItem media) {

        MediaMetadataDto metaData = new MediaMetadataDto();

        Video myVideo = getVideo(media.getSource());

        if(myVideo != null){
            VideoInfo info = myVideo.getVideoInfo();
            if(info != null){
                metaData.setResolution(info.NativeResolution);
                metaData.setBitRate(info.BitRate);
                metaData.setDuration(info.Duration);
            }
        }

        return metaData;
    }

    @Override
    public boolean canHandle(MediaItem media) {
        return media.getType() == MediaType.MOVIE;
    }



    private Video getVideo(MediaSource source){
        Video video = null;
        if(source != null && source.isAvailable())
        {
            ResourceLocation path = source.getResourceLocation();
            video = new Video(path, videoAccessService);
        }
        return video;
    }
}
