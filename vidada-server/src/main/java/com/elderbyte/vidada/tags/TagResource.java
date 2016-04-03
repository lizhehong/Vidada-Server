package com.elderbyte.vidada.tags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * REST controller for managing tags.
 */
@Secured({"ROLE_USER"})
@RestController
@RequestMapping("/api/tags")
public class TagResource {

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    @Autowired
    private TagService tagService;

    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Tag> getAll() {
        return tagService.findAllTags();
    }

    @RequestMapping(value = "used",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Tag> getAllUsed() {
        return tagService.findAllUsedTags();
    }

}
