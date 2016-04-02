package com.elderbyte.vidada.vidada.xattr;

import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.NotSupportedException;
import com.elderbyte.vidada.vidada.media.ImageMediaItem;
import com.elderbyte.vidada.vidada.media.MediaItem;
import com.elderbyte.vidada.vidada.media.MovieMediaItem;
import com.elderbyte.vidada.vidada.media.Resolution;
import com.elderbyte.vidada.vidada.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Synchronizes the media metadata values of a media object with the extended attributes of the file.
 *
 */
@Deprecated // TODO import of xattr is handled by an agent, writing them down should probably be handled in media-service.save()...
@Service
public class MetadataSynchronizer {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final XAttrMetadataService xattrMetadataService;

    @Autowired
    public MetadataSynchronizer(XAttrMetadataService xattrMetadataService){
        this.xattrMetadataService = xattrMetadataService;
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

        if(xattrMetadataService.isMetaDataSupported(physicalFile)){

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

        if(xattrMetadataService.isMetaDataSupported(physicalFile)){

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
            xattrMetadataService.writeMetaData(physicalFile, KnownXAttr.FileHash, media.getFilehash());
        }

        if(media.getRating() != 0){
            xattrMetadataService.writeMetaData(physicalFile, KnownXAttr.Rating, media.getRating()+"");
        }

        String tags = tagsToString(media.getTags());
        if(tags != null && !tags.isEmpty()) {
            xattrMetadataService.writeMetaData(physicalFile, KnownXAttr.Tags, tags);
        }

    }

    private void writeMetaDataMovie(MovieMediaItem media, ResourceLocation physicalFile) {
        writeMetaDataBasic(media, physicalFile);

        if(media.getBitrate() != 0) {
            xattrMetadataService.writeMetaData(physicalFile, KnownXAttr.Bitrate, media.getBitrate()+"");
        }

        if(media.getDuration() != 0) {
            xattrMetadataService.writeMetaData(physicalFile, KnownXAttr.Duration, media.getDuration()+"");
        }

        if(media.hasResolution()) {
            xattrMetadataService.writeMetaData(physicalFile, KnownXAttr.Resolution, media.getResolution().toString());
        }
    }

    private void writeMetaDataImage(ImageMediaItem media, ResourceLocation physicalFile) {
        writeMetaDataBasic(media, physicalFile);
    }


    private boolean updateFromMetaDataBasic(MediaItem media, ResourceLocation physicalFile) {

        boolean updated = false;

        if(media.getFilehash() == null){
            String hashStr = xattrMetadataService.readMetaData(physicalFile, KnownXAttr.FileHash);
            media.setFilehash(hashStr);
            updated = true;
        }

        if(media.getRating() == 0){
            String ratingStr = xattrMetadataService.readMetaData(physicalFile, KnownXAttr.Rating);
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

        String tagsStr = xattrMetadataService.readMetaData(physicalFile, KnownXAttr.Tags);
        Set<Tag> tags = parseTagString(tagsStr);
        media.getTags().addAll(tags);

        return updated;
    }

    private boolean updateFromMetaDataMovie(MovieMediaItem media, ResourceLocation physicalFile) {
        boolean updated = updateFromMetaDataBasic(media, physicalFile);

        if(media.getBitrate() == 0) {
            String bitrateStr = xattrMetadataService.readMetaData(physicalFile, KnownXAttr.Bitrate);
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
            String durationStr = xattrMetadataService.readMetaData(physicalFile, KnownXAttr.Duration);
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
            String resolutionStr = xattrMetadataService.readMetaData(physicalFile, KnownXAttr.Resolution);
            if(resolutionStr != null){
                Resolution resolution = Resolution.ofString(resolutionStr);
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
            String resolutionStr = xattrMetadataService.readMetaData(physicalFile, KnownXAttr.Resolution);
            if(resolutionStr != null){
                Resolution resolution = Resolution.ofString(resolutionStr);
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

    private Set<Tag> parseTagString(String tagsStr){
        String[] tokens = tagsStr.split(",");
        return Tag.buildTags(tokens);
    }

}
