package com.elderbyte.vidada.security;

import org.springframework.security.core.AuthenticationException;


public class InvalidSignatureException extends AuthenticationException {
    public InvalidSignatureException(String s) {
        super(s);
    }
}
