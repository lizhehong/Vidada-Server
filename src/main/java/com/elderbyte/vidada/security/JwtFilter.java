package com.elderbyte.vidada.security;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.nimbusds.jwt.SignedJWT;

public class JwtFilter extends GenericFilterBean {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    AuthenticationManager authenticationManager;

    public JwtFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        System.out.println("JwtFilter.doFilter");
        System.out.println("JwtFilter.authenticationManager"
            + authenticationManager);

        HttpServletRequest req = (HttpServletRequest) request;

        String stringToken = req.getHeader("Authorization");
        System.out.println("JwtFilter.doFilter.stringToken:" + stringToken);

        if (stringToken != null && stringToken != "") {
            try {
                SignedJWT sjwt = SignedJWT.parse(stringToken);
                JwtToken token = new JwtToken(sjwt);
                Authentication auth = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ParseException e) {
                logger.error("Parsing JWT Token failed!", e);
            } catch (AuthenticationException e) {
                logger.error("Authentication failed!", e);
            }
        }

        chain.doFilter(request, response);
    }


}
