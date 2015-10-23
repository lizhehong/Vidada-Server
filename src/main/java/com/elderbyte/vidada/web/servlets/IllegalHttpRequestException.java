package com.elderbyte.vidada.web.servlets;

import javax.servlet.http.HttpServletResponse;

/**
 * Thrown when a web HTTP request was illegal.
 *
 */
public class IllegalHttpRequestException extends Exception {



    public IllegalHttpRequestException(String message){
        super(message);
    }


}
