package com.elderbyte.vidada.web.rest;

import archimedes.core.data.pagination.ListPage;
import com.elderbyte.vidada.domain.media.*;
import com.elderbyte.vidada.domain.tags.Tag;
import com.elderbyte.vidada.domain.tags.TagUtil;
import com.elderbyte.vidada.security.JwtFilter;
import com.elderbyte.vidada.service.media.MediaService;
import com.elderbyte.vidada.web.rest.dto.MediaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Secured({"ROLE_USER"})
@RestController
@RequestMapping("/api/medias")
public class MediasResource {


    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LoggerFactory.getLogger(MediasResource.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
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
    public ResponseEntity<ListPage<MediaDTO>> getMedias(
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "pageSize", defaultValue = "6")  Integer pageSize,
        @RequestParam(value = "query", defaultValue = "") String queryStr,
        @RequestParam(value = "tagExpression", defaultValue = "") String tagExpression,
        @RequestParam(value = "type", defaultValue = "ANY") MediaFilterType type,
        @RequestParam(value = "orderBy", defaultValue = "FILENAME") OrderProperty order,
        @RequestParam(value = "reverse", defaultValue = "false") Boolean reverse,
        @RequestParam(value = "onlyAvailable", defaultValue = "true") Boolean onlyAvailable) {

        MediaQuery query = new MediaQuery();
        query.setKeywords(queryStr);
        query.setTagExpression(tagExpression);
        query.setSelectedtype((type != null) ? type : MediaFilterType.ANY);
        query.setOrder((order != null) ? order : OrderProperty.FILENAME);
        query.setReverseOrder(reverse);
        query.setOnlyAvailable(onlyAvailable);

        logger.debug("Delivering medias page: " + page + " pageSize: " + pageSize);

        ListPage<MediaItem> lp = mediaService.query(query, page, pageSize);

        return Optional.ofNullable(lp)
            .map(p -> new ResponseEntity<>(build(p), HttpStatus.OK))
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
    public ResponseEntity<MediaDTO> getMedia(@PathVariable("hash") String hash) {
        MediaItem media = mediaService.queryByHash(hash);
        return Optional.ofNullable(media)
            .map(m -> ResponseEntity.ok(build(m)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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


    private ListPage<MediaDTO> build(ListPage<MediaItem> page){
        List<MediaDTO> dtos = new ArrayList<>();

        for(MediaItem media : page.getPageItems()){
            dtos.add(build(media));
        }
        return new ListPage<>(dtos, page.getTotalListSize(), page.getMaxPageSize(), page.getPage());
    }

    private MediaDTO build(MediaItem media){

        UriComponents baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build();

        String token = JwtFilter.findAuthToken(request);

        String streamUrl = baseUrl.toUriString() + "/stream/" + media.getFilehash();
        String thumbnailUrl = baseUrl.toUriString() + "/api/thumbs/" + media.getFilehash() + "?jwt="+token;
        media.getType();

        MediaDTO mediaDTO = new MediaDTO(media.getFilehash(), media.getFilename(), media.getType(), thumbnailUrl, streamUrl);

        for(Tag tag : media.getTags()){
            mediaDTO.getTags().add(tag.getName());
        }

        return mediaDTO;
    }



}
