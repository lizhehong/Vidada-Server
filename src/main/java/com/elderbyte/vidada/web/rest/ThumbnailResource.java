package com.elderbyte.vidada.web.rest;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import com.elderbyte.vidada.domain.media.MediaItem;
import com.elderbyte.vidada.domain.media.MovieMediaItem;
import com.elderbyte.vidada.service.ThumbnailService;
import com.elderbyte.vidada.service.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * REST controller for managing tags.
 */
@RestController
@RequestMapping("/api/thumbs")
public class ThumbnailResource {

    private final Logger log = LoggerFactory.getLogger(ThumbnailResource.class);
    private static int MIN_SIZE = 50;
    private static int MAX_SIZE = 1000;

    @Inject
    private ThumbnailService thumbnailService;

    @Inject
    private MediaService mediaService;



    @RequestMapping(value = "{hash}",
        method = RequestMethod.POST)
    public ResponseEntity updateThumb(
        @PathParam("hash") String hash,
        @RequestBody float pos){

        MediaItem media = mediaService.queryByHash(hash);
        if(media != null){
            if(media instanceof MovieMediaItem){
                thumbnailService.renewThumbImage((MovieMediaItem)media, pos);
                return ResponseEntity.accepted().build();
            }else{
                log.error("Can only recreate video thumbs, but this media is not a video: " + media.getType());
                ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
        }
        return ResponseEntity.notFound().build();
    }


    @RequestMapping(value = "{hash}",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPNG(@PathParam("hash") String hash,
                           @RequestParam("width") int width,
                           @RequestParam("height") int height) {
        MediaItem media = mediaService.queryByHash(hash);

        width = Math.max(width, MIN_SIZE);
        height = Math.max(height, MIN_SIZE);
        width = Math.min(width, MAX_SIZE);
        height = Math.min(height, MAX_SIZE);

        Size thumbSize = new Size(width, height);


        final IMemoryImage image = thumbnailService.getThumbImage(media, thumbSize);

        return Optional.ofNullable(image).map(img -> {
                // Create a byte array output stream.
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                try {
                    img.writePNG(bao);
                    return new ResponseEntity<>(bao.toByteArray(), HttpStatus.OK);
                } catch (IOException e) {
                    log.error("Failed to send image byte stream!", e);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        ).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
