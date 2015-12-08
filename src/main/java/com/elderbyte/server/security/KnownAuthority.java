package com.elderbyte.server.security;

/**
 * Holds the known Authorities
 */
public final class KnownAuthority {

    private KnownAuthority(){}

    public static final String USER = "ROLE_USER";

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private static final String[] all = { USER, ADMIN, ANONYMOUS };

    public static String[] values(){ return all; }
}
