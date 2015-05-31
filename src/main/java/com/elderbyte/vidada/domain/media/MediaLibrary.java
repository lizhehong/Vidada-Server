package com.elderbyte.vidada.domain.media;

import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.vidada.domain.ArgumentNullException;
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
import java.nio.file.InvalidPathException;

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
    private String rootPathUri;
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
     * Creates a new media library with the given name at the given location.
     *
     * @param name The name of this library
     * @param location The local root folder of this media.
     */
    public MediaLibrary(String name, DirectoryLocation location){
        this(name, location, false, false);
    }


    /**
     * Creates a new media library with the given name at the given location.
     *
     * @param name The name of this library
     * @param location The local root folder of this media.
     * @param ignoreImages
     * @param ignoreMovies
     */
    public MediaLibrary(String name, DirectoryLocation location, boolean ignoreImages, boolean ignoreMovies) {
        if (name == null) throw new ArgumentNullException("name");
        if(location == null) throw new ArgumentNullException("location");

        this.name = name;
        setRootPath(location.getUri());
        setIgnoreImages(ignoreImages);
        setIgnoreMovies(ignoreMovies);
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
     * Get the root path of this media library.
     * If the library root path is not or wrongly configured, will throw an exception
     * @return Returns the location of this media library
     * @throws MediaLibraryException
     */
    public DirectoryLocation getLibraryRoot() {

        if(libraryDirectoryLocation == null){
            try {
                libraryDirectoryLocation = DirectoryLocation.Factory.create(getRootPath());
            } catch (NotSupportedException | InvalidPathException e) {
                throw new MediaLibraryException("Could not get the library root!" , e);
            }
        }
        return libraryDirectoryLocation;
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private void setRootPath(URI rootPath){
        rootPathUri = rootPath.toString();
    }

    private URI getRootPath(){

        if(rootPathUri == null) throw new NotSupportedException("Empty rootPathUrl is not allowed!");

        try {
            return new URI(rootPathUri);
        } catch (URISyntaxException e) {
            throw new NotSupportedException("Can not create URI instance from uri string: '" + rootPathUri +"'", e);
        }
    }

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
            ", rootPathUrl='" + rootPathUri + '\'' +
            ", ignoreMovies=" + ignoreMovies +
            ", ignoreImages=" + ignoreImages +
            '}';
    }
}
