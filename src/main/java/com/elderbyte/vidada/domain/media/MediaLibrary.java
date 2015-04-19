package com.elderbyte.vidada.domain.media;

import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.vidada.domain.entities.IdEntity;
import com.elderbyte.vidada.domain.images.cache.IImageCache;
import com.elderbyte.vidada.domain.images.cache.ImageCacheFactory;
import com.elderbyte.vidada.domain.media.extracted.IMediaPropertyStore;
import com.elderbyte.vidada.domain.media.extracted.JsonMediaPropertyStore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import java.beans.Transient;
import java.io.File;
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
    public static final String VidataThumbsFolder = VidataCacheFolder + "/thumbs";
    public static final String VidataInfoFolder = VidataCacheFolder + "/info";
    public static final String VidataTagRelations = VidataCacheFolder + "/tags.txt";

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private String libraryRootURI;
    private boolean ignoreMovies;
    private boolean ignoreImages;

    transient private IImageCache imageCache = null;
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

    public MediaLibrary(DirectoryLocation location){
        setLibraryRoot(location);
    }

    public MediaLibrary(File location){
        setLibraryRoot(DirectoryLocation.Factory.create(location));
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

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
     * Set the root path of this media library
     * @param location
     */
    public void setLibraryRoot(DirectoryLocation location) {
        this.libraryDirectoryLocation = location;
        libraryRootURI = libraryDirectoryLocation.getUri().toString();
    }

    /**
     * Get the root path of this media library
     * @return
     */
    public DirectoryLocation getLibraryRoot() {
        if(libraryDirectoryLocation == null){
            try {
                libraryDirectoryLocation = DirectoryLocation.Factory.create(libraryRootURI);
            } catch (URISyntaxException e) {
                logger.error(e);
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
     * Gets this library's thumbnail cache
     * @return Returns the thumbnail cache

    public synchronized IImageCache getLibraryCache(){

        if(imageCache == null){
            DirectoryLocation libraryRoot = getLibraryRoot();
            if(libraryRoot != null && libraryRoot.exists()){
                try {
                    DirectoryLocation libCache = DirectoryLocation.Factory.create(libraryRoot, VidataThumbsFolder);
                    logger.info("Opening new library cache...");
                    imageCache = ImageCacheFactory.instance().openCache(libCache); // TODO Remove this
                } catch (URISyntaxException e1) {
                    logger.error(e1);
                }
            }
        }
        return imageCache;
    } */

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
    public String toString(){
        DirectoryLocation root = getLibraryRoot();
        return (root != null ? root.toString() : "MediaLibrary:: DirectoiryLocation=NULL") + " id: " + getId();
    }

}
