package com.elderbyte.vidada.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {



    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {



        throw new UsernameNotFoundException("Cant find user '" + userName + "'");
    }
}
