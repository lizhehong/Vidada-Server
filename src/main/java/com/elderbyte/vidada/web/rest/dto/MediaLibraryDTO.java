package com.elderbyte.vidada.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents the data of a media library
 *
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MediaLibraryDTO {

    private int id;
    private String name;
    private String rootPath;
    private boolean isAvailabe;
    private boolean ignoreMusic;
    private boolean ignoreVideos;
    private boolean ignoreImages;

    /**
     * Empty JSON deserializer constructor
     */
    protected MediaLibraryDTO(){ }

    public MediaLibraryDTO(int id, String name, String rootPath, boolean isAvailabe,
                           boolean ignoreMusic, boolean ignoreVideos, boolean ignoreImages) {
        this.id = id;
        this.name = name;
        this.rootPath = rootPath;
        this.isAvailabe = isAvailabe;
        this.ignoreMusic = ignoreMusic;
        this.ignoreVideos = ignoreVideos;
        this.ignoreImages = ignoreImages;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The root path of the media library
     * @return
     */

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public boolean isIgnoreMovies() {
        return ignoreVideos;
    }

    public void setIgnoreMovies(boolean ignoreMovies) {
        this.ignoreVideos = ignoreMovies;
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

    public boolean isAvailabe() {
        return isAvailabe;
    }

    public void setIsAvailabe(boolean isAvailabe) {
        this.isAvailabe = isAvailabe;
    }

    public int getId() {
        return id;
    }

}
