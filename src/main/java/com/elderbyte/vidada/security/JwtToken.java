package com.elderbyte.vidada.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@SuppressWarnings("serial")
public class JwtToken implements Authentication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final SignedJWT sjwt;
    private JWTClaimsSet claims;
    private boolean authenticated;

    public JwtToken(SignedJWT sjwt) {
        this.sjwt = sjwt;
        this.authenticated = false;
        try {
            claims = this.sjwt.getJWTClaimsSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void clearClaims() {
        this.claims = new JWTClaimsSet.Builder().build();
    }

    public SignedJWT getSignedToken() {
        return this.sjwt;
    }

    @Override
    public String getName() {
        return this.claims.getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String claimsString = (String) this.claims.getClaim("roles");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (claimsString != null && claimsString != "") {
            String[] roles = claimsString.split(",");
            logger.info("Found roles: " + roles.length);
            for (String role : roles) {
                if(!role.isEmpty()){
                    logger.info("role: " + role);
                    grantedAuthorities.add(new SimpleGrantedAuthority(role));
                }
            }
        }
        return Collections.unmodifiableList(grantedAuthorities);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getDetails() {
        return claims.toJSONObject();
    }

    @Override
    public Object getPrincipal() {
        return claims.getSubject();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String toString() {
        return "JwtToken{" +
            "sjwt=" + sjwt +
            ", principal=" + getPrincipal() +
            ", claims=" + claims +
            ", authenticated=" + authenticated +
            '}';
    }
}
