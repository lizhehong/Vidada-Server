package com.elderbyte.vidada.security;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccessDeniedExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(value = AccessDeniedException.class)
    public void handle(HttpServletResponse reponse) {

        logger.warn("Handling AccessDeniedException!");

        reponse.addHeader("X-Unauthorized", "1");
        reponse.setStatus(200);
    }
}