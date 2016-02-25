package com.elderbyte.server.security.jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${security.oauth2.jwt.key}")
    private String secretKey; // TODO Use key strategy

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if(secretKey == null) throw new IllegalStateException("The JWT signing key is not set (=null). Ensure you set the property security.oauth2.jwt.key!");


        JwtToken jwtToken = (JwtToken) authentication;
        try {
            JWSVerifier verifier = new MACVerifier(secretKey);
            boolean isVerified = jwtToken.getSignedToken().verify(verifier);
            if (isVerified) {

                // The token is valid -  check if it is activated and not expired
                Date expirationDate = jwtToken.getSignedToken().getJWTClaimsSet().getExpirationTime();
                if(expirationDate != null){
                    if(LocalDateTime.now().isAfter(toDateTime(expirationDate))){
                        // The token has expired!
                        throw new JwtAuthenticationException("Authentication failed - The provided token is valid but already expired!");
                    }
                }

                Date notValidBefore = jwtToken.getSignedToken().getJWTClaimsSet().getNotBeforeTime();
                if(notValidBefore != null){
                    if(LocalDateTime.now().isBefore(toDateTime(notValidBefore))){
                        // The token is not yet valid!
                        throw new JwtAuthenticationException("Authentication failed - The provided token is valid not yet valid (notValidBefore is set to " + toDateTime(notValidBefore).toString() + "!");
                    }
                }

                jwtToken.setAuthenticated(true);
            } else {
                throw new JwtAuthenticationException("Authentication failed - MAC signature not matching!");
            }
            return jwtToken;
        } catch (JOSEException e) {
            throw new JwtAuthenticationException("Authentication failed", e);
        } catch (ParseException e) {
            throw new JwtAuthenticationException("Authentication failed - Could not read expiration header!", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    private static LocalDateTime toDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
