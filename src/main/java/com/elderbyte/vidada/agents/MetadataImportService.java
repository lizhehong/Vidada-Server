package com.elderbyte.vidada.agents;


import com.elderbyte.vidada.media.MediaItem;
import com.elderbyte.vidada.media.MediaService;
import com.elderbyte.vidada.media.MovieMediaItem;
import com.elderbyte.vidada.tags.Tag;
import com.elderbyte.vidada.tags.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Service
public class MetadataImportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ForkJoinPool mainPool = new ForkJoinPool(3);

    @Autowired
    private MediaAgentService mediaAgentService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private TagService tagService;


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Updates all media metadata
     */
    public synchronized void updateAllMediaMetadataAsync() {
        List<MediaItem> medias = mediaService.findAllMedias();
        for (MediaItem m : medias) {
            updateMetaDataAsync(m, false).exceptionally(e -> {
                logger.error("While updating meta-data an unexpected exception occured!", e);
                return null;
            });
        }
    }


    /**
     * Updates the metadata of the given media async
     * @param media
     * @return
     */
    public CompletableFuture updateMetaDataAsync(MediaItem media, boolean force){
        return submitTask(() -> updateMetaDataAndSave(media, force));
    }


    /**
     * Update the metadata of this media
     * @param media
     */
    @Transactional
    public void updateMetaDataAndSave(MediaItem media, boolean force){
        MediaItem myMedia = mediaService.findById(media.getFilehash()).orElse(null);
        if(myMedia != null) {
            if (updateMetaData(myMedia, force)) {

                logger.info("Updated metadata of media " + media + "...");
                mediaService.save(myMedia);
                media.prototype(myMedia);
            }
        }
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    private CompletableFuture submitTask(Runnable task){
        return CompletableFuture.runAsync(task, mainPool);
    }

    private boolean updateMetaData(MediaItem media, boolean force){

        boolean updated = false;

        Iterable<MediaAgent> agents = mediaAgentService.findAllAgents();

        for (MediaAgent agent : agents) {
            try {
                if(agent.canHandle(media)){
                    MediaMetadataDto metaData = agent.fetchMetadata(media);
                    updated = updated | updateWith(media, metaData);
                }
            }catch (Exception e){
                logger.error(String.format("Media-Agent %s failed!", agent), e);
            }
        }
        return updated;
    }

    /**
     * Update the media item propeties with the new metaData
     * @param media
     * @param metaData
     * @return Returns true if there has been a property updated
     */
    private boolean updateWith(MediaItem media, MediaMetadataDto metaData){

        boolean updated = false;

        if(metaData.getResolution() != null && !metaData.getResolution().isEmpty()) {
            media.setResolution(metaData.getResolution());
            updated = true;
        }

        updated = updated | updateTags(media, metaData.getGenres());

        metaData.getCast(); // TODO import as tags?
        metaData.getProductionCompanies(); // TODO import as tags?

        switch (media.getType()){
            case MOVIE:
                updated = updated | updateWith((MovieMediaItem)media, metaData);
                break;
            case IMAGE:
                // Images have no special properties yet
                break;
        }

        return updated;
    }

    private boolean updateTags(MediaItem media, Set<String> tags){
        boolean updated = false;
        for (String tagName : tags){
            Tag tag = tagService.findOrCreateTag(tagName);
            if(tag != null){
                if(media.getTags().add(tag)){
                    updated = true;
                }
            }
        }
        return updated;
    }

    /**
     * Update the movie specific item propeties with the new metaData
     * @param media
     * @param metaData
     */
    private boolean updateWith(MovieMediaItem media, MediaMetadataDto metaData){
        boolean updated = false;

        if(metaData.getBitRate() > 0) {
            media.setBitrate(metaData.getBitRate());
            updated = true;
        }

        if(metaData.getDuration() > 0) {
            media.setDuration(metaData.getDuration());
            updated = true;
        }

        return updated;
    }


}
