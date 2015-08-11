package com.elderbyte.vidada.web.rest;

import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.factories.DirectoryLocationFactory;
import com.elderbyte.vidada.domain.media.MediaLibrary;
import com.elderbyte.vidada.service.MediaLibraryService;
import com.elderbyte.vidada.web.rest.dto.MediaDTO;
import com.elderbyte.vidada.web.rest.dto.MediaLibraryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.websocket.server.PathParam;
import java.net.URI;
import java.util.ArrayList;
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
    public List<MediaLibraryDTO> getAllLibraries(){
       return buildDtos(mediaLibraryService.getAllLibraries());
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
            .map(m -> buildDto(m))
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

            DirectoryLocation libraryRoot = DirectoryLocation.Factory.create(new URI(newLibraryDto.getRootPath()));
            MediaLibrary newLibrary = new MediaLibrary(newLibraryDto.getName(), libraryRoot);

            mediaLibraryService.addLibrary(newLibrary);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            logger.error("Could not create new library " + newLibraryDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private MediaLibraryDTO buildDto(MediaLibrary library){
        return new MediaLibraryDTO(library.getName(), library.getLibraryRoot().getUriString());
    }

    private List<MediaLibraryDTO> buildDtos(Iterable<MediaLibrary> libraries){
        List<MediaLibraryDTO> dtos = new ArrayList<>();
        libraries.forEach(l -> dtos.add(buildDto(l)));
        return dtos;
    }


}
