package com.elderbyte.vidada.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@RestController
public class AccountController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private String SecretKey = "494847a9c8a147bf82f4ca6da59efe61"; // TODO

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Valid @RequestBody LoginDto user) {

        logger.info("User attempts to login: " + user);

        try {
            JWSSigner signer = new MACSigner(SecretKey);
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

            // TODO Check if the user exists and fetch his authorities / roles
            builder.subject(user.username);
            builder.issuer("myself");
            builder.claim("roles", "ROLE_ADMIN");

            builder.expirationTime(new Date(new Date().getTime() + 24 * 60 * 60  * 1000));
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(
                JWSAlgorithm.HS256), builder.build());

            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (JOSEException e) {
            logger.error("Login failed!", logger);
        }
        return "";
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleException(MethodArgumentNotValidException exception) {
        return exception.getMessage();
    }
}
