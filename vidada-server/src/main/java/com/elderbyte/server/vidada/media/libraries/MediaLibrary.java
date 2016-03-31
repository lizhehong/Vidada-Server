package com.elderbyte.server.vidada.media.libraries;

import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.common.NotSupportedException;
import com.elderbyte.server.vidada.entities.IdEntity;
import com.elderbyte.server.vidada.media.MediaDirectory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents an local MediaLibrary folder.
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
     * Each vidada library has a hidden folder for cache and configuration
     */
    private static final String VidataCacheFolder = ".vidada-library";
    public static final String VidataTagRelations = VidataCacheFolder + "/tags.txt";

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    @NotNull
    private String name;

    @NotNull
    @Size(max = 1000)
    @Column(length = 1000)
    private String rootPathUri;

    private boolean ignoreMovies;
    private boolean ignoreImages;

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

        if(name == null) throw new ArgumentNullException("name");
        if(location == null) throw new ArgumentNullException("location");

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
     * Copy all values from the given prototype to this instance
     * @param prototype
     */
    public void prototype(MediaLibrary prototype) {
        this.setName(prototype.getName());
        this.setLibraryRoot(prototype.getLibraryRoot());

        this.setIgnoreImages(prototype.isIgnoreImages());
        this.setIgnoreMovies(prototype.isIgnoreMovies());
    }

    /**
     * Gets the media directory which represents the root of this media library.
     * @throws MediaLibraryException
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
        this.rootPathUri = location.getUriString();
    }

    /**
     * Get the root path of this media library.
     * If the library root path is not or wrongly configured, will throw an exception
     * @return Returns the location of this media library
     * @throws MediaLibraryException
     */
    public DirectoryLocation getLibraryRoot() {

        if(rootPathUri == null) throw new NotSupportedException("rootPath must not be null!");

        if(libraryDirectoryLocation == null){
            try {
                URI rootPath = new URI(rootPathUri);
                libraryDirectoryLocation = DirectoryLocation.Factory.create(rootPath);
            } catch (URISyntaxException e) {
                logger.error("rootpath was not a valid URI", e);
            }
        }
        return libraryDirectoryLocation;
    }

    /**
     * Returns the meta-data folder of this media-library.
     * Usually a hidden folder which contains configuration and caches.
     * @return
     */
    public DirectoryLocation getLibraryMetadataFolder(){
        DirectoryLocation libraryRoot = getLibraryRoot();
        if(libraryRoot != null){
            try {
                return DirectoryLocation.Factory.create(libraryRoot, VidataCacheFolder);
            } catch (URISyntaxException e) {
                logger.error("rootpath was not a valid URI", e);
            }
        }
        return null;
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
     * Is this library / root path available?
     * @return
     */
    @Transient
    public boolean isAvailable(){
        try{
            DirectoryLocation root = getLibraryRoot();
            return root != null && root.exists();
        }catch (Exception e){
            logger.error("Availability check failed." , e);
            return false;
        }
    }

    @Override
    public String toString() {
        return "MediaLibrary{" +
            "id='" + getId() + '\'' +
            "name='" + name + '\'' +
            ", rootPathUrl='" + rootPathUri + '\'' +
            ", ignoreMovies=" + ignoreMovies +
            ", ignoreImages=" + ignoreImages +
            '}';
    }


}
