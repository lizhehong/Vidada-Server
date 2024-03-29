package com.elderbyte.vidada.media;


import com.elderbyte.common.ArgumentNullException;
import com.elderbyte.common.ListPage;
import com.elderbyte.vidada.security.jwt.JwtFilter;
import com.elderbyte.vidada.dto.AsyncResourceDTO;
import com.elderbyte.vidada.dto.MediaDTO;
import com.elderbyte.vidada.images.IMemoryImage;
import com.elderbyte.vidada.tags.Tag;
import com.elderbyte.vidada.tags.TagService;
import com.elderbyte.vidada.thumbnails.ThumbnailResource;
import com.elderbyte.vidada.thumbnails.ThumbnailService;
import com.elderbyte.vidada.web.servlets.MediaStreamServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private TagService tagService;

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Transactional
    @RequestMapping(
        value = "{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@RequestBody MediaDTO mediaDto){
        try {

            MediaItem existing = mediaService.findById(mediaDto.getId()).orElse(null);
            if(existing != null){
                MediaDTO.updateFromDto(existing, mediaDto);
                mediaService.save(existing);
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
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
        @RequestParam(value = "orderBy", required = false) OrderProperty order,
        @RequestParam(value = "reverse", required = false) boolean reverse,
        @RequestParam(value = "onlyAvailable", defaultValue = "true") Boolean onlyAvailable) {

        MediaQuery query = new MediaQuery();
        query.setKeywords(queryStr);
        query.setTagExpression(tagExpression);
        query.setSelectedtype((type != null) ? type : MediaFilterType.ANY);
        query.setOrder((order != null) ? order : OrderProperty.TITLE);
        query.setReverseOrder(reverse);
        query.setOnlyAvailable(onlyAvailable);

        logger.debug("Query medias page: " + page + " pageSize: " + pageSize);

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
        return mediaService.findById(hash)
            .map(m -> ResponseEntity.ok(build(m)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Deletes the given media local file permanently
     * @param hash
     * @return
     */
    @RequestMapping(value = "{hash}",
        method = RequestMethod.DELETE)
    public ResponseEntity deleteMedia(@PathVariable("hash") String hash) {
       Optional<MediaItem> mediaItem = mediaService.findById(hash);

        if(mediaItem.isPresent()){

            try {

                mediaService.deleteLocalFile(mediaItem.get());
                return ResponseEntity.ok().build();

            }catch (Exception e){
                logger.error("Failed to delete media " + hash + "!", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Parses the tags query param string into Tag objects
     * @param tagsParam
     * @return
     */
    private Set<Tag> parseTags(String tagsParam){

        Set<Tag> tags = new HashSet<>();

        if(tagsParam != null) {
            String[] tagTokens = parseMultiValueParam(tagsParam);
            tags = Tag.buildTags(tagTokens);
        }

        return tags;
    }

    protected final String[] parseMultiValueParam(String multiParams){
        return multiParams != null ? multiParams.split(" ") : null;
    }


    private ListPage<MediaDTO> build(ListPage<MediaItem> page){

        if(page == null) throw new ArgumentNullException("page");

        List<MediaDTO> dtos = new ArrayList<>();

        for(MediaItem media : page.getPageItems()){
            dtos.add(build(media));
        }
        return new ListPage<>(dtos, page.getTotalListSize(), page.getMaxPageSize(), page.getPage());
    }

    private MediaDTO build(MediaItem media){

        if(media == null) throw new ArgumentNullException("media");

        MediaDTO mediaDTO = new MediaDTO(media, buildThumbnailAsync(media), MediaStreamServlet.getAbsoluteStreamUrl(media));
        return mediaDTO;
    }


    private AsyncResourceDTO buildThumbnailAsync(MediaItem media){
        AsyncResourceDTO asyncResource;

        float thumbPosition = media instanceof MovieMediaItem ? ((MovieMediaItem)media).getThumbnailPosition() : 0;

        CompletableFuture<IMemoryImage> imageTask = thumbnailService.getThumbnailAsync(media, thumbPosition);
        if(imageTask.isDone()){
            // The thumbnail has been processed for this media, create matching url

            UriComponentsBuilder thumbnailUrl = linkTo(
                methodOn(ThumbnailResource.class).getPNG(media.getFilehash(), thumbPosition, null, null))
                .toUriComponentsBuilder()
                .queryParam("jwt", JwtFilter.findAuthToken(request));

            asyncResource = AsyncResourceDTO.ofResource(thumbnailUrl.toUriString());
        }else{
            // Thumbnail has not yet been created
            asyncResource = AsyncResourceDTO.Processing;
        }

        return asyncResource;
    }





}
