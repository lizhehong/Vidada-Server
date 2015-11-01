package com.elderbyte.vidada.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private String SecretKey = "494847a9c8a147bf82f4ca6da59efe61"; // TODO Use key strategy

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JwtToken jwtToken = (JwtToken) authentication;
        logger.info("JWT authentication with token: " + jwtToken);
        try {
            JWSVerifier verifier = new MACVerifier(SecretKey);
            boolean isVerified = jwtToken.getSignedToken().verify(verifier);
            if (isVerified) {
                logger.info("JWT is verified and authenticated!");
                jwtToken.setAuthenticated(true);
            } else {
                throw new JwtAuthenticationException("Authentication failed - MAC not matching!");
            }
            return jwtToken;
        } catch (JOSEException e) {
            throw new JwtAuthenticationException("authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
