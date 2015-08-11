package com.elderbyte.vidada.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents the data of a media library
 */
@JsonAutoDetect(// We use fields for JSON (de)serialisation
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE)
public class MediaLibraryDTO {

    private String name;
    private String rootPath;
    private boolean ignoreMovies;
    private boolean ignoreImages;


    protected MediaLibraryDTO(){

    }

    public MediaLibraryDTO(String name, String rootPath){
        setName(name);
        setRootPath(rootPath);
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
        return ignoreMovies;
    }

    public void setIgnoreMovies(boolean ignoreMovies) {
        this.ignoreMovies = ignoreMovies;
    }

    public boolean isIgnoreImages() {
        return ignoreImages;
    }

    public void setIgnoreImages(boolean ignoreImages) {
        this.ignoreImages = ignoreImages;
    }


}
