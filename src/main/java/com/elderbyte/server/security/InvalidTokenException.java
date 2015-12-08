package com.elderbyte.server.security;

import org.springframework.security.core.AuthenticationException;


public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException(String s) {
        super(s);
    }
}
