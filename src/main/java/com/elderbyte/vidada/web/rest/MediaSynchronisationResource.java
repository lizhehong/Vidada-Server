package com.elderbyte.vidada.web.rest;

import com.elderbyte.vidada.service.sync.MediaImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Represents the media synchronisation task which indexes the known
 * media library directories.
 */
@RestController
@RequestMapping("/api/synchronisation")
public class MediaSynchronisationResource {

    private static final Logger logger = LoggerFactory.getLogger(MediaSynchronisationResource.class);

    @Inject
    private MediaImportService importService;


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
