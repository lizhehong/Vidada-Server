package com.elderbyte.vidada.domain.media;

import archimedes.core.io.locations.LocationFilters;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.io.locations.filters.ILocationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides helper methods to deal with media files
 * and extensions.
 */
public class MediaTypeUtil {

    /***************************************************************************
     *                                                                         *
     * Private static fields                                                   *
     *                                                                         *
     **************************************************************************/

    private final static Map<MediaType, String[]> mediaTypeExtensions = new HashMap<MediaType, String[]>();
    private final static Map<MediaType, ILocationFilter> typeFilters = new HashMap<MediaType, ILocationFilter>();

    /***************************************************************************
     *                                                                         *
     * Static Constructor                                                      *
     *                                                                         *
     **************************************************************************/

    static{

        // ToDo load from config files...

        // Mostly compiled from
        // http://en.wikipedia.org/wiki/Comparison_of_container_formats

        String[] knownMovieExtensions = {
                ".3gp", ".3g2", ".avi", ".aaf", ".asf",
                ".wmv", ".divx",
                ".vob", ".evo", ".m2p", ".ps", ".ts", ".m2ts", ".mts",
                ".mxf",
                ".mkv", ".mk3d", ".mks",
                ".mcf",
                ".vlc",
                ".mp4", ".mpg", ".mpeg", ".m4v",
                ".mov", ".qt", ".rm" ,".rmvb",
                ".webm",
                ".f4v", ".flv",
        };

        String[] knownImageExtensions = {
                ".png", ".bmp", ".jpg", ".jpeg", ".gif",
                ".exif", ".tiff",
                ".raw",
                ".ppm",".pgm", ".pbm", ".pnm",
                ".webp",
                ".hdr",
                // Vector formats
                ".svg"
        };

        mediaTypeExtensions.put(MediaType.MOVIE, knownMovieExtensions);
        mediaTypeExtensions.put(MediaType.IMAGE, knownImageExtensions);
    }


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     *
     */
	private MediaTypeUtil(){
	}


    /***************************************************************************
     *                                                                         *
     * Static API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Returns all file extensions for the given media type
     * @param type
     * @return
     */
    private static String[] getExtensions(MediaType type){
        return mediaTypeExtensions.get(type);
    }


    /**
     * Creates a file-location filter which only accepts the given media type
     * @param type
     * @return
     */
    public synchronized static ILocationFilter getPathFilter(MediaType type) {

        ILocationFilter fileFilter = typeFilters.get(type);
        if(fileFilter == null){
            String[] extensions = getExtensions(type);
            fileFilter = LocationFilters.extensionFilter(extensions);
            typeFilters.put(type, fileFilter);
        }
        return fileFilter;
    }

    /**
     * Checks if this file is of the given media type.
     * @param file
     * @param type
     * @return
     */
	public static boolean isFileOfType(ResourceLocation file, MediaType type){
		return getPathFilter(type).accept(file);
	}

}
