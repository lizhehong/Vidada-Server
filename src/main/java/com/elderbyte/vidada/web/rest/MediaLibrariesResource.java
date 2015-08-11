package com.elderbyte.vidada.web.rest;

import archimedes.core.io.locations.DirectoryLocation;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.service.MediaLibraryService;
import com.elderbyte.vidada.web.rest.dto.MediaLibraryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
    public List<MediaLibraryDTO> getAllLibraries(){
       return mediaLibraryService.getAllLibraries().stream()
           .map(x -> createDto(x)).collect(Collectors.toList());
    }

    /**
     * Gets the given media library
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaLibraryDTO> getLibrary(@PathVariable("id") Integer id) {
        MediaLibrary library = mediaLibraryService.getById(id);
        return Optional.ofNullable(library)
            .map(m -> createDto(m))
            .map(m -> ResponseEntity.ok(m))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Deletes the given media library
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}",
        method = RequestMethod.DELETE)
    public ResponseEntity deleteLibrary(@PathVariable("id") int id) {
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
    public ResponseEntity createLibrary(@RequestBody MediaLibraryDTO newLibraryDto){
        try {
            logger.info("Creating new media library " + newLibraryDto);
            mediaLibraryService.addLibrary(createLibraryFromDTO(newLibraryDto));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            logger.error("Could not create new library " + newLibraryDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private MediaLibrary createLibraryFromDTO(MediaLibraryDTO dto) throws URISyntaxException{
        String rootPathUri = dto.getRootPath();
        DirectoryLocation libraryRoot = DirectoryLocation.Factory.create(new URI(rootPathUri));
        return new MediaLibrary(
            dto.getName(),
            libraryRoot);
    }

    private MediaLibraryDTO createDto(MediaLibrary library){
        return new MediaLibraryDTO(
            library.getName(),
            library.getLibraryRoot().getUriString(),
            true, /** ignore music**/
            library.isIgnoreMovies(),
            library.isIgnoreImages());
    }


}
