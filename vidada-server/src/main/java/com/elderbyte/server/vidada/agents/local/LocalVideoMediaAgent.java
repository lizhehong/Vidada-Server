package com.elderbyte.server.vidada.agents.local;

import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.ffmpeg.VideoInfo;
import com.elderbyte.server.vidada.agents.MediaAgent;
import com.elderbyte.server.vidada.agents.MediaMetadataDto;
import com.elderbyte.server.vidada.media.MediaItem;
import com.elderbyte.server.vidada.media.MediaType;
import com.elderbyte.server.vidada.media.MovieMediaItem;
import com.elderbyte.server.vidada.media.source.MediaSource;
import com.elderbyte.server.vidada.video.IVideoAccessService;
import com.elderbyte.server.vidada.video.Video;
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
        if(media.getType() == MediaType.MOVIE){
            MovieMediaItem movie = (MovieMediaItem)media;
            if(movie.hasResolution() && movie.getBitrate() > 0 && movie.getDuration() > 0){
                return false; // All meta-data are already filled in.
            }
            return true; // We might have more meta-data
        }
        return false;
    }

    @Override
    public void refresh() {

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
