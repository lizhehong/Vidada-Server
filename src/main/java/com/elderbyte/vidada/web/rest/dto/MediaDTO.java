package com.elderbyte.vidada.web.rest.dto;

import com.elderbyte.vidada.domain.media.MediaItem;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents a
 */
@JsonAutoDetect(// We use fields for JSON (de)serialisation
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE)
public class MediaDTO {

    private String hash;
    private String name;
    private String thumbnailUrl;
    private String streamUrl;

    public MediaDTO(String hash, String name, String thumbnailUrl, String streamUrl) {
        this.hash = hash;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.streamUrl = streamUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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
}
