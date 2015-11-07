package com.elderbyte.vidada.security;

import com.elderbyte.vidada.security.Authority;
import com.elderbyte.vidada.security.KnownAuthority;
import com.elderbyte.vidada.security.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Manages security authorities (roles)
 */
@Service
public class AuthorityService {


    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityService(AuthorityRepository authorityRepository){
        this.authorityRepository = authorityRepository;
        ensureDefaultAuthorities();
    }


    public Authority get(String name){
        return authorityRepository.findOne(name);
    }

    @Transactional
    public Authority create(String authorityName){
        Authority auth = new Authority(authorityName);
        authorityRepository.save(auth);
        return auth;
    }


    /**
     * Ensures that all known authorities exist in the database
     */
    @Transactional
    private void ensureDefaultAuthorities(){
        for(String role : KnownAuthority.values()) {
            Authority auth = get(role);
            if (auth == null) {
                create(role);
            }
        }
    }


}
