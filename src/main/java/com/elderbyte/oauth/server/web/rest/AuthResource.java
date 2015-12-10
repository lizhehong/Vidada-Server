package com.elderbyte.oauth.server.web.rest;

import javax.validation.Valid;

import com.elderbyte.oauth.server.AuthService;
import com.elderbyte.oauth.server.LoginDto;
import com.elderbyte.oauth.server.web.rest.dtos.TokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jwt.SignedJWT;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/auth/login")
public class AuthResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuthService authService;

    /**
     * Checks the given user name and password.
     * If valid, a signed JWT Token is returned which can be used to access the protected API.
     * @param userLogin
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto userLogin) {

        SignedJWT token = authService.login(userLogin.username, userLogin.password);
        if(token != null){
            return ResponseEntity.ok(new TokenDto(token.serialize()));
        }else{
            throw new AuthenticationServiceException("Internal Server error, token was not provided!");
        }
    }


    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleException(MethodArgumentNotValidException exception) {
        logger.warn("Bad Request error", exception);
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public String handleException(AuthenticationException exception) {
        logger.warn("Authentication failed!", exception);
        return exception.getMessage();
    }

}
