package com.elderbyte.server.security;

import org.springframework.security.core.AuthenticationException;


public class InvalidSignatureException extends AuthenticationException {
    public InvalidSignatureException(String s) {
        super(s);
    }
}
