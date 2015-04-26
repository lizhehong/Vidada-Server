package com.elderbyte.vidada.web.rest;

import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.service.MediaLibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/libraries")
public class MediaLibrariesResource {

    private static final Logger logger = LoggerFactory.getLogger(MediaLibrariesResource.class);


    @Inject
    private MediaLibraryService mediaLibraryService;


    /**
     * Gets all libraries
     * @return
     */
    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MediaLibrary> getAllLibraries(){
       return mediaLibraryService.getAllLibraries();
    }

    /**
     * Gets the given media library
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaLibrary> getLibrary(@PathParam("id") long id) {
        MediaLibrary library = mediaLibraryService.getById(id);
        return Optional.ofNullable(library).map(m -> ResponseEntity.ok(m)).orElse(
            new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Deletes the given media library
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}",
        method = RequestMethod.DELETE)
    public ResponseEntity deleteLibrary(@PathParam("id") long id) {
        MediaLibrary library = mediaLibraryService.getById(id);

        if(library != null){

            try {
                mediaLibraryService.removeLibrary(library);
                return ResponseEntity.ok().build();
            }catch (Exception e){
                logger.error("Failed to delete library " +id + "!", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @RequestMapping(
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createLibrary(@RequestBody MediaLibrary newLibrary){
        try {
            mediaLibraryService.addLibrary(newLibrary);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            logger.error("Could not create new library " + newLibrary, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
