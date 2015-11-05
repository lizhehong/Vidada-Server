package com.elderbyte.vidada.web.rest.util;

import com.elderbyte.vidada.security.JwtFilter;

import javax.servlet.http.HttpServletRequest;


/**
 *
 */
public final class TokenUtil {

    /**
     *  Authorizes the given link with the auth token found in the given request.
     * @param link
     * @param request
     * @return
     */
    public static String authenticateLink(String link, HttpServletRequest request){
        String token = JwtFilter.findAuthToken(request);
        return authenticateLink(link, token);
    }


    /**
     * Authorizes the given link with the given token.
     * @param link
     * @param token
     * @return
     */
    public static String authenticateLink(String link, String token){
        if(!link.endsWith("?")){
            link += "?";
        }
        return link + "jwt=" + token;
    }

}
