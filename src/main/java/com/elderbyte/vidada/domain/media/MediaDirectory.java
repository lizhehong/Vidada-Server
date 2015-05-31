package com.elderbyte.vidada.domain.media;


import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.LocationFilters;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.io.locations.UniformLocation;
import archimedes.core.io.locations.filters.IDirectoryFilter;
import archimedes.core.io.locations.filters.ILocationFilter;
import archimedes.core.util.FileSupport;
import archimedes.core.util.Lists;
import com.elderbyte.vidada.domain.ArgumentNullException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.beans.Transient;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract directory containing media files.
 *
 * Provides convenient methods to (recursively) list all media items.
 *
 * This implementation is immutable.
 * @author IsNull
 *
 */
public final class MediaDirectory {

    private static final Logger logger = LogManager.getLogger(MediaDirectory.class.getName());

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

	transient private final DirectoryLocation directory;
	transient private final boolean ignoreMovies;
	transient private final boolean ignoreImages;

	transient private ILocationFilter mediaFilter = null;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

	/**
	 * Creates a new MediaDirectory with the specified filter settings.
	 *
	 * @param directory Root directory
	 * @param ignoreImages Ignore images files
	 * @param ignoreMovies Inore movie files
	 */
	public MediaDirectory(DirectoryLocation directory, boolean ignoreImages, boolean ignoreMovies) {
        if(directory == null) throw new ArgumentNullException("directory");

		this.directory = directory;
		this.ignoreImages = ignoreImages;
		this.ignoreMovies = ignoreMovies;
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Get the location of this media-directory
     * @return
     */
	public DirectoryLocation getDirectory() {
		return directory;
	}

	public boolean isIgnoreMovies() {
		return ignoreMovies;
	}

	public boolean isIgnoreImages() {
		return ignoreImages;
	}

	/**
	 * Is this library available?
	 * @return
	 */
	public boolean isAvailable(){
		return directory != null && directory.exists();
	}

	/**
	 * Constructs the relative path for the given file (must be in a sub directory of this directory!)
	 * @param absoluteLibraryFile
	 * @return Returns the relative path or null, if a relative path could not be created
	 */
	public URI getRelativePath(ResourceLocation absoluteLibraryFile){

		URI relativeFile = FileSupport.getRelativePath(
                absoluteLibraryFile.getUri(),
                directory.getUri());

		if(absoluteLibraryFile.getUri().getPath().equals(relativeFile.getPath()))
			relativeFile = null;

		return relativeFile;
	}

	/**
	 * Returns the absolute path for the given relative file path
	 * @param relativeFile
	 * @return
	 */
	public ResourceLocation getAbsolutePath(URI relativeFile){
		try {
			if(directory != null){
				return  ResourceLocation.Factory.create(
						new URI(directory.getUri() + relativeFile.toString()),
						directory.getCredentials());
			}
		} catch (URISyntaxException e) {
			logger.error(e);
		}
		return null;
	}


	/**
	 * Returns all media files recursively in this library
	 * @return
	 */
	public List<ResourceLocation> getAllMediaFilesRecursive(){
		if(directory != null)
			return Lists.asTypedList(directory.listAll(buildFilter(), ignoreVidadaCache));
		return new ArrayList<ResourceLocation>();
	}

	/**
	 * Returns all media files and folders in this directory
	 * @return
	 */
	public List<UniformLocation> getMediasAndFolders(){
		if(directory != null){

			ILocationFilter filter = LocationFilters.or(
                    LocationFilters.AcceptAllDirs,
                    buildFilter());

			return Lists.asTypedList(directory.listAll(filter));
		}
		return new ArrayList<UniformLocation>();
	}


	/**
	 * Build an {@link ILocationFilter} to filter files
	 * based on media type which are applicable for this media library
	 * @return
	 */
	@Transient
	public ILocationFilter buildFilter(){

		if(mediaFilter == null){

            // We build the filter based on logical AND and OR expressions

            ILocationFilter filter = LocationFilters.DenyAllFilter;

			if(!this.isIgnoreImages())
			{
                filter =  LocationFilters.or(
                        filter,
                        MediaTypeUtil.getPathFilter(MediaType.IMAGE));
			}

			if(!this.isIgnoreMovies())
			{
                filter = LocationFilters.or(
                        filter,
                        MediaTypeUtil.getPathFilter(MediaType.MOVIE));
			}

			mediaFilter = LocationFilters.and(
                    filter,
                    denyMetaDataFilter);
		}
		return mediaFilter;
	}


	/**
	 * A simple filter which does not accept meta data files
     * (AppleDouble-Prefixes) and Vidada's own thumb cache folders etc.
	 */
	private transient static final ILocationFilter denyMetaDataFilter = new ILocationFilter() {

		private static final String AppleDoublePrefix = "._";
		private final int AppleDoublePrefixLen = AppleDoublePrefix.length();

		@Override
		public boolean accept(UniformLocation file) {

			if(file instanceof ResourceLocation)
			{
				String name = file.getName();
				if(name.length() >= AppleDoublePrefixLen){
					return !name.substring(0, AppleDoublePrefixLen).equals(AppleDoublePrefix);
				}
			}

			return true;
		}
	};

	/**
	 * A simple filter to ignore Vidada generated cache items in the media library
	 *
	 */
	private transient static final IDirectoryFilter ignoreVidadaCache = new IDirectoryFilter() {
		@Override
		public boolean accept(DirectoryLocation directoiry) {
			String name = directoiry.getName();
			return !name.equals(MediaLibrary.VidataCacheFolder);
		}
	};



}
