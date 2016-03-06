package com.elderbyte.oauth.server.web.rest.dtos;

import com.elderbyte.oauth.server.User;
import com.elderbyte.oauth.server.authorities.Authority;

import java.util.ArrayList;
import java.util.List;

/**
 * A DTO which provides detailed user information.
 */
public class UserDto {

    public String login;
    public List<String> roles = new ArrayList<>();

    protected UserDto() { }

    public UserDto(User user){

        login = user.getLogin();

        for(Authority authority : user.getAuthorities()){
            roles.add(authority.getName());
        }
    }
}
