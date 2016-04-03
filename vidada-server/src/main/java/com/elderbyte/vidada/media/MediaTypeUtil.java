package com.elderbyte.vidada.media;

import com.elderbyte.common.locations.LocationFilters;
import com.elderbyte.common.locations.ResourceLocation;
import com.elderbyte.common.locations.filters.ILocationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides helper methods to deal with media files
 * and extensions.
 */
public final class MediaTypeUtil {

    /***************************************************************************
     *                                                                         *
     * Private static fields                                                   *
     *                                                                         *
     **************************************************************************/

    private final static Map<MediaType, String[]> mediaTypeExtensions = new HashMap<>();
    private final static Map<String, MediaType> extensionMediaType = new HashMap<>();


    private final static Map<MediaType, ILocationFilter> typeFilters = new HashMap<>();

    /***************************************************************************
     *                                                                         *
     * Static Constructor                                                      *
     *                                                                         *
     **************************************************************************/

    static{

        // ToDo load from config files...

        // Mostly compiled from
        // http://en.wikipedia.org/wiki/Comparison_of_container_formats

        String[] knownVideoExtensions = {
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

        mediaTypeExtensions.put(MediaType.MOVIE, knownVideoExtensions);
        mediaTypeExtensions.put(MediaType.IMAGE, knownImageExtensions);

        for(String imageExt : knownImageExtensions){
            extensionMediaType.put(imageExt.toLowerCase(), MediaType.IMAGE);
        }

        for(String videoExt : knownVideoExtensions){
            extensionMediaType.put(videoExt.toLowerCase(), MediaType.MOVIE);
        }

    }


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     *
     */
	private MediaTypeUtil(){ }


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

    public static MediaType findTypeByFilter(MediaFilterType filter){
        switch (filter){

            case IMAGE:
                return MediaType.IMAGE;

            case MOVIE:
                return MediaType.MOVIE;

            default:
                return MediaType.UNKNOWN;

        }
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

    public static MediaType findTypeByResource(ResourceLocation resourceLocation) {
        String extensionWithDot = resourceLocation.getExtension().toLowerCase();
        if(extensionMediaType.containsKey(extensionWithDot)){
            return extensionMediaType.get(extensionWithDot);
        }
        return MediaType.UNKNOWN;
    }
}
