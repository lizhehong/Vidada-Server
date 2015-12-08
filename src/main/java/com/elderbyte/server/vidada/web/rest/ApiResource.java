package com.elderbyte.server.vidada.web.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
public class ApiResource {



    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.TEXT_PLAIN_VALUE)
    public String getHelloApi() {
        return "Welcome to to Vidada REST API!";
    }


}
