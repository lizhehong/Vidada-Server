package com.elderbyte.oauth.server;

/**
 * User credentials
 */
public class LoginDto {
    public String username;
    public String password;

    @Override
    public String toString() {
        return "LoginDto{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
