package com.elderbyte.vidada.domain.security;

/**
 * Holds the known Authorities
 */
public final class KnownRole {

    private KnownRole(){}

    public static final String USER = "ROLE_USER";

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private static final String[] all = { USER, ADMIN, ANONYMOUS };

    public static String[] values(){ return all; }
}
