package com.elderbyte.server.security;


public class TokenDto {
    public String token;

    protected TokenDto(){ }

    public TokenDto(String token){ this.token = token; }

    @Override
    public String toString() {
        return "TokenDto{" +
            "token='" + token + '\'' +
            '}';
    }
}
