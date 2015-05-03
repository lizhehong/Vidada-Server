package com.elderbyte.vidada.web.rest;

import archimedes.core.data.pagination.ListPage;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MediaQuery;
import com.elderbyte.vidada.domain.media.OrderProperty;
import com.elderbyte.vidada.domain.tags.Tag;
import com.elderbyte.vidada.domain.tags.TagUtil;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import java.util.*;


@RestController
@RequestMapping("/api/medias")
public class MediasResource {


    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LoggerFactory.getLogger(MediasResource.class);


    @Inject
    private MediaService mediaService;

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @RequestMapping(
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@RequestBody MediaItem media){
        try {
            mediaService.update(media);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.error("Failed to update media!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListPage<MediaItem>> getMedias(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "pageSize", defaultValue = "6")  int pageSize,
        @RequestParam(value = "query", defaultValue = "") String queryStr,
        @RequestParam(value = "tags", defaultValue = "") String requiredTags,
        @RequestParam(value = "tagsNot", defaultValue = "") String blockedTags,
        @RequestParam(value = "type", defaultValue = "ANY") com.elderbyte.vidada.domain.media.MediaType type,
        @RequestParam(value = "orderBy", defaultValue = "FILENAME") OrderProperty order,
        @RequestParam(value = "reverse", defaultValue = "false") boolean reverse) {

        MediaQuery query = new MediaQuery();
        query.setKeywords(queryStr);

        query.getRequiredTags().addAll(parseTags(requiredTags));
        query.getBlockedTags().addAll(parseTags(blockedTags));

        query.setSelectedtype((type != null) ? type : com.elderbyte.vidada.domain.media.MediaType.ANY);
        query.setOrder((order != null) ? order : OrderProperty.FILENAME);
        query.setReverseOrder(reverse);

        logger.info("Delivering medias page: " + page + " pageSize: " + pageSize);

        ListPage<MediaItem> lp = mediaService.query(query, page, pageSize);

        return Optional.ofNullable(lp)
            .map( p -> new ResponseEntity<>(p, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * Returns the number of medias
     * Use vidada/api/medias/count
     * to get the total number of medias
     * @return
     */
    @RequestMapping(value = "count",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCount() {
        return String.valueOf(mediaService.count());
    }

    /**
     * Gets the detail of a single media
     * @param hash
     * @return
     */
    @RequestMapping(value = "{hash}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaItem> getMedia(@PathParam("hash") String hash) {
        MediaItem media = mediaService.queryByHash(hash);
        return Optional.ofNullable(media).map(m -> ResponseEntity.ok(m)).orElse(
           new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }




    /**
     * Parses the tags query param string into Tag objects
     * @param tagsParam
     * @return
     */
    private Set<Tag> parseTags(String tagsParam){

        Set<Tag> tags = new HashSet<>();

        if(tagsParam != null) {
            String[] tagTokens = parseMultiValueParam(tagsParam);
            tags = TagUtil.createTags(tagTokens);
        }

        return tags;
    }

    protected final String[] parseMultiValueParam(String multiParams){
        return multiParams != null ? multiParams.split(" ") : null;
    }
}
