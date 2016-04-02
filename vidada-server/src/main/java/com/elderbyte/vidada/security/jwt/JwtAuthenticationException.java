package com.elderbyte.vidada.security.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown when a JWT Token was not valid or authentication was denied.
 */
@SuppressWarnings("serial")
public class JwtAuthenticationException extends AuthenticationException {


    public JwtAuthenticationException(String msg) {
        super(msg);
    }

    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
