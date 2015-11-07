package com.elderbyte.vidada.security;

import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/auth/login")
public class AccountController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    private String SecretKey = "494847a9c8a147bf82f4ca6da59efe61"; // TODO


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto userLogin) {

        logger.info("User attempts to login: " + userLogin);

        try {
            // Check if the user exists and fetch his authorities / roles

            User user = userService.getUser(userLogin.username);

            if(user != null){

                JWSSigner signer = new MACSigner(SecretKey);
                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

                builder.subject(user.getLogin());
                builder.issuer("myself");
                builder.claim("roles", Authority.toFlatString(user.getAuthorities()));
                builder.expirationTime(new Date(new Date().getTime() + 24 * 60 * 60  * 1000));
                SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), builder.build());
                signedJWT.sign(signer);

                logger.info("User logged in successfully!");

                return ResponseEntity.ok(new TokenDto(signedJWT.serialize()));
            }else{
                logger.warn("No active user found with name '" + userLogin.username + "'");
            }
        } catch (JOSEException e) {
            logger.error("Login failed!", e);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }



    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleException(MethodArgumentNotValidException exception) {
        return exception.getMessage();
    }

}
