package com.elderbyte.vidada.dto;

import com.elderbyte.vidada.media.MediaItem;
import com.elderbyte.vidada.media.MediaType;
import com.elderbyte.vidada.media.MovieMediaItem;
import com.elderbyte.vidada.media.Resolution;
import com.elderbyte.vidada.tags.Tag;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the data of a media
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MediaDTO {

    private String id;
    private int duration;
    private int bitrate;
    private float thumbnailPosition;
    private String title;
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
        this.title = media.getTitle();
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

        if(media instanceof MovieMediaItem){
            MovieMediaItem movieMeida = (MovieMediaItem)media;
            this.duration = movieMeida.getDuration();
            this.bitrate = movieMeida.getBitrate();
            this.thumbnailPosition = movieMeida.getThumbnailPosition();
        }
    }



    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public float getThumbnailPosition() {
        return thumbnailPosition;
    }

    public void setThumbnailPosition(float thumbnailPosition) {
        this.thumbnailPosition = thumbnailPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public AsyncResourceDTO getThumbnailResource() {
        return thumbnailResource;
    }

    public void setThumbnailResource(AsyncResourceDTO thumbnailResource) {
        this.thumbnailResource = thumbnailResource;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public Resolution getResolution() {
        return Resolution.ofString(resolution);
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution.toString();
    }

    public static void updateFromDto(MediaItem existing, MediaDTO mediaDto) {
        existing.setTitle(mediaDto.getTitle());
        existing.setRating(mediaDto.getRating());
        if(existing instanceof MovieMediaItem){
            ((MovieMediaItem) existing).setThumbnailPosition(mediaDto.getThumbnailPosition());
        }
    }
}
