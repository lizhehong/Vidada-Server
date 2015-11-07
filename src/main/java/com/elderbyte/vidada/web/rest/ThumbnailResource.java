package com.elderbyte.vidada.web.rest;

import archimedes.core.images.IMemoryImage;
import com.elderbyte.vidada.media.MediaItem;
import com.elderbyte.vidada.media.Resolution;
import com.elderbyte.vidada.thumbnails.ThumbnailService;
import com.elderbyte.vidada.media.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * REST controller for managing tags.
 */
@Secured({"ROLE_USER"})
@RestController
@RequestMapping("/api/thumbs")
public class ThumbnailResource {

    private final Logger log = LoggerFactory.getLogger(ThumbnailResource.class);

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private MediaService mediaService;


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /*
    @RequestMapping(value = "{hash}",
        method = RequestMethod.POST)
    public ResponseEntity updateThumb(
        @PathVariable("hash") String hash,
        @RequestBody float pos){

        MediaItem media = mediaService.findById(hash);
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
    */


    /**
     * Returns a thumbnail image for the given media
     * @param hash The media id (hash)
     * @param position The relative position of the frame [0.0-1.0] if this media is a movie.
     * @param width
     * @param height
     * @return
     */
    @RequestMapping(value = "{hash}",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getPNG(
                            @PathVariable("hash") String hash,
                            @RequestParam(value = "position", defaultValue = "0") Float position,
                            @RequestParam(value = "width", defaultValue = "250") Integer width,
                            @RequestParam(value = "height", defaultValue = "180") Integer height) {

        MediaItem media = mediaService.findById(hash).orElse(null);


        if(media == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Resolution thumbSize = new Resolution(width, height);

        final CompletableFuture<IMemoryImage> imageTask = thumbnailService.getThumbnailAsync(media, thumbSize, position);

        if(imageTask.isDone()){
            // If the requested thumbnail is available, send it to the client

            try {
                IMemoryImage image = imageTask.get();

                if(image != null){
                    // Create a byte array output stream.
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    image.writePNG(bao);
                    return new ResponseEntity<>(bao.toByteArray(), HttpStatus.OK);
                }else{
                    return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
                }

            } catch (InterruptedException e) {
                log.error("Interrupted!", e);
                return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (ExecutionException e) {
                log.error("Failed to extract thumbnail!", e);
                return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                log.error("Failed to send image byte stream!", e);
                return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else {
            // Still waiting for the thumb to be extracted - this url is not (yet) valid!
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }
    }

}
