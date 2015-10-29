package com.elderbyte.vidada.web.rest.dto;

import com.elderbyte.vidada.domain.media.MediaType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

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

    protected MediaDTO(){ }


    public MediaDTO(String id, String name, MediaType mediaType, String thumbnailUrl, String streamUrl) {
        this.id = id;
        this.name = name;
        this.mediaType = mediaType;
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
}
