package com.elderbyte.server.web;

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
