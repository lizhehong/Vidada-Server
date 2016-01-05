package com.elderbyte.oauth.server.authorities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;


/**
 * Manages security authorities (roles)
 */
@Service
public class AuthorityService {


    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityService(AuthorityRepository authorityRepository){
        this.authorityRepository = authorityRepository;
    }


    @PostConstruct
    @Transactional
    public void init(){
        ensureDefaultAuthorities();
    }

    @Transactional
    public Authority findByName(String name){
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
    private void ensureDefaultAuthorities(){
        for(String role : KnownAuthority.values()) {
            Authority auth = findByName(role);
            if (auth == null) {
                create(role);
            }
        }
    }


}
