package com.elderbyte.vidada.web.rest.dto;

import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaType;
import com.elderbyte.vidada.domain.media.Resolution;
import com.elderbyte.vidada.domain.tags.Tag;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the data of a media
 */
@JsonAutoDetect(// We use fields for JSON (de)serialisation
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE)
public class MediaDTO {

    private String id;
    private String name;
    private MediaType mediaType;
    private List<String> tags = new ArrayList<>();
    private int rating;
    private String resolution;
    private int timesOpened;
    private long fileSize;
    private ZonedDateTime addedDate;
    private ZonedDateTime lastAccessedDate;

    // Urls
    private AsyncResourceDTO thumbnailResource;
    private String streamUrl;


    /**
     * Empty constructor for JSON
     */
    protected MediaDTO(){ }


    /**
     * Creates a new DTO from the domain entity
     * @param media
     * @param thumbnailResource
     * @param streamUrl
     */
    public MediaDTO(MediaItem media, AsyncResourceDTO thumbnailResource, String streamUrl) {

        this.id = media.getFilehash();
        this.name = media.getFilename();
        this.mediaType = media.getType();

        for(Tag tag : media.getTags()){
            this.tags.add(tag.getName());
        }

        this.fileSize = media.getFileSize();
        this.addedDate = media.getAddedDate();
        this.lastAccessedDate = media.getLastAccessed();
        this.rating = media.getRating();
        setResolution(media.getResolution());
        this.timesOpened = media.getOpened();
        this.thumbnailResource = thumbnailResource;
        this.streamUrl = streamUrl;
    }

    public Resolution getResolution() {
        return Resolution.ofString(resolution);
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution.toString();
    }
}
