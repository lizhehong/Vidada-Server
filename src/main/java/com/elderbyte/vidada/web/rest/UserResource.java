package com.elderbyte.vidada.web.rest;

import com.elderbyte.vidada.security.User;
import com.elderbyte.vidada.security.UserRepository;
import com.elderbyte.vidada.security.PrincipalDto;
import com.elderbyte.vidada.security.SecurityUtils;
import com.elderbyte.vidada.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing users.
 */
@Secured({"ROLE_USER"})
@RestController
@RequestMapping("/api/users")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET  /users -> get all users.
     */
    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        log.debug("REST request to get all Users");
        return userRepository.findAll();
    }

    /**
     * GET  /users/:login -> get the "login" user.
     */
    @RequestMapping(value = "{login}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<User> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userRepository.findOneByLogin(login)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @RequestMapping(
        value = "current", // /api/users/current
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrincipalDto> getCurrentPrincipal() {

        String login = SecurityUtils.getCurrentLogin();

        log.info("Current Login from JWT: " + login);

        User user = userService.getUser(login);

        if(user != null){
            log.info("REST request to get current User details: '" + login + "'");
            return new ResponseEntity<>(new PrincipalDto(user), HttpStatus.OK);
        }else{
            log.info("REST request to get current User, but could not find a user with this name: '" + login + "'");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
