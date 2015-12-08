package com.elderbyte.server.security;

public class CredentialUtil {

	/**
	 * Turns the given string into a domain identifier
	 * @param string
	 * @return
	 */
	public static String toDomain(String authority, String string){
		// replace all Non-AlphaNumeric chars with a dot
		return authority + "." + string.replaceAll("\\W|_", ".");
	}
}
