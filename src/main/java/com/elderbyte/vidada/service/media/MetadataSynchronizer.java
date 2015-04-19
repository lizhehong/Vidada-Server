package com.elderbyte.vidada.service.media;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.geometry.Size;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.domain.media.ImageMediaItem;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MovieMediaItem;
import com.elderbyte.vidada.domain.metadata.MediaMetaAttribute;
import com.elderbyte.vidada.domain.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizes the media metadata values of a media object with the extended attributes of the file.
 *
 */
@Service
public class MetadataSynchronizer {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final MediaMetaDataService metaDataService;

    @Inject
    public MetadataSynchronizer(MediaMetaDataService metaDataService){
        this.metaDataService = metaDataService;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Updates this media objects properties from metadata found in the physical file
     * @param media
     * @param physicalFile
     */
    public boolean updateFromMetaData(MediaItem media, ResourceLocation physicalFile){

        if(metaDataService.isMetaDataSupported(physicalFile)){

            switch (media.getType()){
                case IMAGE:
                    return updateFromMetaDataImage((ImageMediaItem) media, physicalFile );

                case MOVIE:
                    return updateFromMetaDataMovie( (MovieMediaItem) media, physicalFile);

                default:
                    throw new NotSupportedException("Not supported media type: " + media.getType());
            }
        }
        return false;
    }



    /**
     * Write metadata information to the physical file
     * @param media
     * @param physicalFile
     */
    public void writeMetaData(MediaItem media, ResourceLocation physicalFile){

        if(metaDataService.isMetaDataSupported(physicalFile)){

            switch (media.getType()){
                case IMAGE:
                    writeMetaDataImage((ImageMediaItem) media, physicalFile);

                case MOVIE:
                    writeMetaDataMovie((MovieMediaItem) media, physicalFile);

                default:
                    throw new NotSupportedException("Not supported media type: " + media.getType());
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    private void writeMetaDataBasic(MediaItem media, ResourceLocation physicalFile){

        if(media.getFilehash() != null){
            metaDataService.writeMetaData(physicalFile, MediaMetaAttribute.FileHash, media.getFilehash());
        }

        if(media.getRating() != 0){
            metaDataService.writeMetaData(physicalFile, MediaMetaAttribute.Rating, media.getRating()+"");
        }

        String tags = tagsToString(media.getTags());
        if(tags != null && !tags.isEmpty()) {
            metaDataService.writeMetaData(physicalFile, MediaMetaAttribute.Tags, tags);
        }

    }

    private void writeMetaDataMovie(MovieMediaItem media, ResourceLocation physicalFile) {
        writeMetaDataBasic(media, physicalFile);

        if(media.getBitrate() != 0) {
            metaDataService.writeMetaData(physicalFile, MediaMetaAttribute.Bitrate, media.getBitrate()+"");
        }

        if(media.getDuration() != 0) {
            metaDataService.writeMetaData(physicalFile, MediaMetaAttribute.Duration, media.getDuration()+"");
        }

        if(media.hasResolution()) {
            metaDataService.writeMetaData(physicalFile, MediaMetaAttribute.Resolution, media.getResolution().toString());
        }
    }

    private void writeMetaDataImage(ImageMediaItem media, ResourceLocation physicalFile) {
        writeMetaDataBasic(media, physicalFile);
    }


    private boolean updateFromMetaDataBasic(MediaItem media, ResourceLocation physicalFile) {

        boolean updated = false;

        if(media.getFilehash() == null){
            String hashStr = metaDataService.readMetaData(physicalFile, MediaMetaAttribute.FileHash);
            media.setFilehash(hashStr);
            updated = true;
        }

        if(media.getRating() == 0){
            String ratingStr = metaDataService.readMetaData(physicalFile, MediaMetaAttribute.Rating);
            if(ratingStr != null){
                try {
                    int rating = Integer.parseInt(ratingStr);
                    media.setRating(rating);
                    updated = true;
                }catch (NumberFormatException e){
                    LOG.warn("Cant parse rating in metadata. Ignored", e);
                }
            }
        }

        String tagsStr = metaDataService.readMetaData(physicalFile, MediaMetaAttribute.Tags);
        List<Tag> tags = parseTagString(tagsStr);
        media.getTags().addAll(tags);

        return updated;
    }

    private boolean updateFromMetaDataMovie(MovieMediaItem media, ResourceLocation physicalFile) {
        boolean updated = updateFromMetaDataBasic(media, physicalFile);

        if(media.getBitrate() == 0) {
            String bitrateStr = metaDataService.readMetaData(physicalFile, MediaMetaAttribute.Bitrate);
            if(bitrateStr != null){
                try {
                    int bitrate = Integer.parseInt(bitrateStr);
                    media.setBitrate(bitrate);
                    updated = true;
                }catch (NumberFormatException e){
                    LOG.warn("Cant parse bitrate in metadata. Ignored", e);
                }
            }
        }


        if(media.getDuration() == 0) {
            String durationStr = metaDataService.readMetaData(physicalFile, MediaMetaAttribute.Duration);
            if(durationStr != null){
                try {
                    int duration = Integer.parseInt(durationStr);
                    media.setDuration(duration);
                    updated = true;
                }catch (NumberFormatException e){
                    LOG.warn("Cant parse duration in metadata. Ignored", e);
                }
            }
        }

        if(!media.hasResolution()) {
            String resolutionStr = metaDataService.readMetaData(physicalFile, MediaMetaAttribute.Resolution);
            if(resolutionStr != null){
                Size resolution = Size.parse(resolutionStr);
                if(resolution != null) {
                    media.setResolution(resolution);
                    updated = true;
                }
            }
        }
        return updated;
    }

    private boolean updateFromMetaDataImage(ImageMediaItem media, ResourceLocation physicalFile) {
        boolean updated = updateFromMetaDataBasic(media, physicalFile);

        if(!media.hasResolution()) {
            String resolutionStr = metaDataService.readMetaData(physicalFile, MediaMetaAttribute.Resolution);
            if(resolutionStr != null){
                Size resolution = Size.parse(resolutionStr);
                if(resolution != null) {
                    media.setResolution(resolution);
                    updated = true;
                }
            }
        }
        return updated;
    }


    private String tagsToString(Iterable<Tag> tags){
        StringBuilder tagBuilder = new StringBuilder();

        for(Tag tag : tags) {
            tagBuilder.append(tag);
            tagBuilder.append(",");
        }

        return tagBuilder.toString();
    }

    private List<Tag> parseTagString(String tagsStr){
        String[] tokens = tagsStr.split(",");
        List<Tag> tags = new ArrayList<>(tokens.length);
        for(String tagToken : tokens){
            if(tagToken != null && !tagToken.isEmpty()) {
                Tag t = Tag.FACTORY.createTag(tagToken);
                tags.add(t);
            }
        }
        return tags;
    }

}
