package com.elderbyte.vidada.media;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.io.locations.ResourceLocation;
import com.elderbyte.vidada.media.libraries.MediaLibrary;
import com.elderbyte.vidada.tags.Tag;

import java.util.Collection;
import java.util.List;

/**
 * Created by IsNull on 19.04.15.
 */
public interface MediaRepositoryCustom {


    /**
     * Query for all medias which match the given media-query
     * @param qry
     * @param pageIndex Page Index
     * @param maxPageSize
     * @return
     */
    ListPage<MediaItem> query(MediaExpressionQuery qry, int pageIndex, final int maxPageSize);

    /**
     * Returns all media items which are in the given libraries
     * @param libraries
     * @return
     */
    List<MediaItem> query(Collection<MediaLibrary> libraries);

    /**
     * Query for all medias which have the given Tag
     */
    Collection<MediaItem> query(Tag tag);

    /**
     * Returns all medias which are part of the given library
     * @param library
     * @return
     */
    List<MediaItem> queryByLibrary(MediaLibrary library);



    /**
     * Query for the first media item with the given path
     * @param file Full file path to the media
     * @param library The library in which this media is
     * @return
     */
    MediaItem queryByPath(ResourceLocation file, MediaLibrary library);


}
