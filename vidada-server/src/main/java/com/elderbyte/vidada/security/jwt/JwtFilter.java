package com.elderbyte.vidada.security.jwt;

import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

/**
 * Filters each HTTP request and looks for a JWT authentication token.
 *
 * If a token is present, it parses the token and sets the security context authentication
 * accordingly. This way, the next HTTP handlers down the chain will have a valid authentication
 * handy.
 *
 */
public class JwtFilter extends GenericFilterBean {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    AuthenticationManager authenticationManager;

    public JwtFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String stringToken = findAuthToken(req);

        logger.trace("HTTP Request with token: " + stringToken);

        // Check if we have an Authorization header

        if (stringToken != null && !stringToken.isEmpty()) {

            try {
                SignedJWT sjwt = SignedJWT.parse(stringToken);
                JwtToken token = new JwtToken(sjwt);
                Authentication auth = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                // JWT was valid and this request is now authenticated!

            } catch (ParseException e) {
                logger.error("Parsing JWT Token failed! Token: '" + stringToken + "'", e);
            } catch (AuthenticationException e) {
                logger.error("Authentication failed!", e);
            }
        }else{
            logger.debug("HTTP request without authorisation!");
        }

        chain.doFilter(request, response);
    }



    /**
     * Extract the auth token from the request.
     * Support Authorization headers and ?jwt parameter
     * @param request
     * @return
     */
    public static String findAuthToken(HttpServletRequest request){

        String stringToken = request.getHeader("Authorization");
        if(stringToken == null || stringToken.isEmpty()){
            // No Authorization Header was found. Maybe a simple url parameter was used
            stringToken = request.getParameter("jwt");
        }

        if(stringToken != null){
            // Clean the JWT token

            // Remove the 'Bearer' prefix, if any.
            stringToken = stringToken.replace("Bearer", "").trim();
        }

        return stringToken;
    }

}
