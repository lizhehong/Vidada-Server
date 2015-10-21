package com.elderbyte.vidada.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.elderbyte.vidada.domain.tags.Tag;
import com.elderbyte.vidada.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collection;

/**
 * REST controller for managing tags.
 */
@RestController
@RequestMapping("/api/tags")
public class TagResource {

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    @Inject
    private TagService tagService;

    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Collection<Tag> getAll() {
        return tagService.getAllTags();
    }

    @RequestMapping(value = "used",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Collection<Tag> getAllUsed() {
        return tagService.getUsedTags();
    }

}
