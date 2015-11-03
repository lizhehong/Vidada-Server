package com.elderbyte.vidada.web.rest.dto;

import archimedes.core.geometry.Size;
import archimedes.core.util.Lists;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaType;
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
    private String thumbnailUrl;
    private String streamUrl;
    private List<String> tags = new ArrayList<>();
    private int rating;
    private Size resolution;
    private int timesOpened;
    private long fileSize;
    private ZonedDateTime addedDate;
    private ZonedDateTime lastAccessedDate;


    /**
     * Empty constructor for JSON
     */
    protected MediaDTO(){ }


    /**
     * Creates a new DTO from the domain entity
     * @param media
     * @param thumbnailUrl
     * @param streamUrl
     */
    public MediaDTO(MediaItem media, String thumbnailUrl, String streamUrl) {

        this.id = media.getFilehash();
        this.name = media.getFilename();
        this.mediaType = media.getType();

        for(Tag tag : media.getTags()){
            this.getTags().add(tag.getName());
        }

        this.fileSize = media.getFileSize();
        this.addedDate = media.getAddedDate();
        this.lastAccessedDate = media.getLastAccessed();
        this.rating = media.getRating();
        this.resolution = media.getResolution();
        this.timesOpened = media.getOpened();
        this.thumbnailUrl = thumbnailUrl;
        this.streamUrl = streamUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Size getResolution() {
        return resolution;
    }

    public void setResolution(Size resolution) {
        this.resolution = resolution;
    }

    public int getTimesOpened() {
        return timesOpened;
    }

    public void setTimesOpened(int timesOpened) {
        this.timesOpened = timesOpened;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public ZonedDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(ZonedDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public ZonedDateTime getLastAccessedDate() {
        return lastAccessedDate;
    }

    public void setLastAccessedDate(ZonedDateTime lastAccessedDate) {
        this.lastAccessedDate = lastAccessedDate;
    }
}
