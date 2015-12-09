package com.elderbyte.server.security;

import org.springframework.security.core.AuthenticationException;

@Deprecated
public class TokenExpiredException extends AuthenticationException {
    public TokenExpiredException(String s) {
        super(s);
    }
}
