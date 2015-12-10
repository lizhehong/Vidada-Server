package com.elderbyte.oauth.server;

import com.elderbyte.oauth.server.authorities.Authority;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * This service provides the ability to check a user login and generate JWT Token if the login succeeded.
 */
@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Value("${security.oauth2.jwt.key}")
    private String secretKey;
    private String issuerId = "elderOAuth";

    /**
     * Log-in the given user / pass and return a JWT Token.
     * @param username
     * @param password
     * @return
     * @throws AuthenticationException Thrown when the credentials are not valid.
     */
    public SignedJWT login(String username, String password) throws AuthenticationException{

        logger.info("User "+username+" attempts to login!");

        try {
            // Check if the user exists and fetch his authorities / roles
            Optional<User> user = userService.getUserWithCredentials(username, password);

            if(user.isPresent()){

                // Issue a valid auth token
                JWSSigner signer = new MACSigner(secretKey);
                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

                builder.subject(user.get().getLogin());
                builder.issuer(issuerId);
                builder.claim("roles", Authority.toFlatString(user.get().getAuthorities()));
                builder.expirationTime(new Date(new Date().getTime() + 24 * 60 * 60  * 1000));
                SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), builder.build());
                signedJWT.sign(signer);

                logger.info("User '"+username+"'logged in successfully!");

                return signedJWT;

            }else{
               throw new BadCredentialsException("No user found with given name / password: " + username);
            }
        } catch (JOSEException e) {
            throw new AuthenticationServiceException("Failed to authenticate user!", e);
        }
    }

}
