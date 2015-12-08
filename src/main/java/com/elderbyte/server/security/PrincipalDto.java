package com.elderbyte.server.security;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PrincipalDto {

    public String login;
    public List<String> roles = new ArrayList<>();

    protected PrincipalDto() { }

    public PrincipalDto(User user){

        login = user.getLogin();

        for(Authority authority : user.getAuthorities()){
            roles.add(authority.getName());
        }
    }
}
