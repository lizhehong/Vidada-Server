package com.elderbyte.vidada.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *
 */
@JsonAutoDetect(// We use fields for JSON (de)serialisation
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE)
public class MediaLibraryDTO {

    String name;
    String rootPath;
    boolean ignoreMusic;
    boolean ignoreVideos;
    boolean ignoreImages;

    public MediaLibraryDTO(String name, String rootPath, boolean ignoreMusic, boolean ignoreVideos, boolean ignoreImages) {
        this.name = name;
        this.rootPath = rootPath;
        this.ignoreMusic = ignoreMusic;
        this.ignoreVideos = ignoreVideos;
        this.ignoreImages = ignoreImages;
    }

    public MediaLibraryDTO(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public boolean isIgnoreMusic() {
        return ignoreMusic;
    }

    public void setIgnoreMusic(boolean ignoreMusic) {
        this.ignoreMusic = ignoreMusic;
    }

    public boolean isIgnoreVideos() {
        return ignoreVideos;
    }

    public void setIgnoreVideos(boolean ignoreVideos) {
        this.ignoreVideos = ignoreVideos;
    }

    public boolean isIgnoreImages() {
        return ignoreImages;
    }

    public void setIgnoreImages(boolean ignoreImages) {
        this.ignoreImages = ignoreImages;
    }
}
