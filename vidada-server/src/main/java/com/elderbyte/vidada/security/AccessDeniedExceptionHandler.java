package com.elderbyte.vidada.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class AccessDeniedExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(value = AccessDeniedException.class)
    public void handle(HttpServletResponse reponse) throws IOException {

        logger.warn("Handling AccessDeniedException!");

        reponse.addHeader("X-Unauthorized", "1");
        //reponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        reponse.sendError(HttpStatus.UNAUTHORIZED.value(), "You are not allowed to access this api");
    }
}
