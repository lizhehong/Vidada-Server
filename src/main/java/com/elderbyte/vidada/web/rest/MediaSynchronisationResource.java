package com.elderbyte.vidada.web.rest;

import com.elderbyte.vidada.synchronisation.MediaSynchronisationService;
import com.elderbyte.vidada.web.rest.dto.SynchronisationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Represents the media synchronisation task which indexes the known
 * media library directories.
 */
@Secured({"ROLE_USER"})
@RestController
@RequestMapping("/api/synchronisation")
public class MediaSynchronisationResource {

    private static final Logger logger = LoggerFactory.getLogger(MediaSynchronisationResource.class);

    @Autowired
    private MediaSynchronisationService importService;



    /**
     * Gets all libraries
     * @return
     */
    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public SynchronisationDTO getSynchronisationStatus(){
        return new SynchronisationDTO(importService.isBusy());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity updateLibraries(){
        try {
            importService.synchronizeAll();
            return ResponseEntity.accepted().build();
        }catch (Exception e){
            logger.error("Failed to synchronize media libraries!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
