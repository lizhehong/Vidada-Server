package com.elderbyte.vidada.domain.media;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.vidada.domain.entities.IdEntity;
import com.elderbyte.vidada.domain.media.extracted.IMediaPropertyStore;
import com.elderbyte.vidada.domain.media.extracted.JsonMediaPropertyStore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents an local user MediaLibrary folder.
 *
 * @author IsNull
 *
 */
@Entity
@Access(AccessType.FIELD)
public class MediaLibrary extends IdEntity {

    /***************************************************************************
     *                                                                         *
     * Private static fields                                                   *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaLibrary.class.getName());


    /**
     * Vidadas cache directory name in a users library folder root
     */
    public static final String VidataCacheFolder = "vidada.db";
    public static final String VidataInfoFolder = VidataCacheFolder + "/info";
    public static final String VidataTagRelations = VidataCacheFolder + "/tags.txt";

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    @NotNull
    private String name;
    @NotNull
    private String rootPath;
    private boolean ignoreMovies;
    private boolean ignoreImages;

    transient private IMediaPropertyStore propertyStore = null;
    transient private MediaDirectory mediaDirectory = null;
    transient private DirectoryLocation libraryDirectoryLocation = null;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Empty ORM Constructor
     */
    protected MediaLibrary(){ }


    /**
     * Creates a new media library on the local file system
     * @param name The name of this library
     * @param fileLocation The local root folder of this media library
     */
    public MediaLibrary(String name, File fileLocation){
        this(name, DirectoryLocation.Factory.create(fileLocation));
    }

    /**
     * Creates a new media library
     * @param name The name of this library
     * @param location The root folder of this media library
     */
    public MediaLibrary(String name, DirectoryLocation location){
        if(location == null) throw new IllegalArgumentException("location must not be null!");
        this.name = name;
        setLibraryRoot(location);
    }


    /***************************************************************************
     *                                                                         *
     * Public Properties                                                       *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the name of this media library
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Determines if movies are ignored in this folder
     * @return
     */
    public boolean isIgnoreMovies() {
        return ignoreMovies;
    }

    public void setIgnoreMovies(boolean ignoreMovies) {
        this.ignoreMovies = ignoreMovies;
        mediaDirectory = null;
    }

    /**
     * Determines if images are ignored in this folder
     * @return
     */
    public boolean isIgnoreImages() {
        return ignoreImages;
    }

    public void setIgnoreImages(boolean ignoreImages) {
        this.ignoreImages = ignoreImages;
        mediaDirectory = null;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the media directory which represents the root of this media library.
     * @return
     */
    public MediaDirectory getMediaDirectory(){

        if(mediaDirectory == null){
            mediaDirectory = new MediaDirectory(getLibraryRoot(), ignoreImages, ignoreMovies);
        }

        return mediaDirectory;
    }

    /**
     * Sets the root path of this media library
     * @param location
     */
    private void setLibraryRoot(DirectoryLocation location){
        this.rootPath = location.getUriString();
    }

    /**
     * Get the root path of this media library
     * @return
     */
    public DirectoryLocation getLibraryRoot() {

        if(rootPath == null) throw new NotSupportedException("rootPath must not be null!");

        if(libraryDirectoryLocation == null){
            try {
                URI rootPathUri = new URI(rootPath);
                libraryDirectoryLocation = DirectoryLocation.Factory.create(rootPathUri);
            } catch (URISyntaxException e) {
                logger.error("rootpath was not a valid URI", e);
            }
        }
        return libraryDirectoryLocation;
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


    /**
     * Returns the user-tag relation definition file
     * @return
     */
    public File getUserTagRelationDef(){
        return new File(new File(getLibraryRoot().getPath()), VidataTagRelations);
    }

    /**
     * Returns the property cache / store for this media library.
     *
     * @return
     */
    public synchronized IMediaPropertyStore getPropertyStore(){
        if(propertyStore == null){
            DirectoryLocation libraryRoot = getLibraryRoot();
            if(libraryRoot != null && libraryRoot.exists()){
                propertyStore = new JsonMediaPropertyStore(new File(libraryRoot.getPath(), VidataInfoFolder));
            }
        }
        return propertyStore;
    }

    /**
     * Is this library / root path available?
     * @return
     */
    @Transient
    public boolean isAvailable(){
        DirectoryLocation root = getLibraryRoot();
        return root != null && root.exists();
    }

    @Override
    public String toString() {
        return "MediaLibrary{" +
            "id='" + getId() + '\'' +
            "name='" + name + '\'' +
            ", rootPath='" + rootPath + '\'' +
            ", ignoreMovies=" + ignoreMovies +
            ", ignoreImages=" + ignoreImages +
            '}';
    }
}
