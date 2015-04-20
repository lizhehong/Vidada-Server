package com.elderbyte.vidada.domain.security;


import archimedes.core.security.CredentialType;
import archimedes.core.security.Credentials;
import archimedes.core.services.IService;

public interface ICredentialManager {

	/**
	 * Delegate to check credentials
	 * @author IsNull
	 *
	 */
	interface ICredentialsChecker {
		/**
		 * Check the given credentials
		 * @param credentials
		 * @return
		 */
		boolean check(Credentials credentials);
	}

	/**
	 * Callback delegate for authentication details
	 * @author IsNull
	 *
	 */
	interface ICredentialsProvider {
		/**
		 * Requests user authentication which usually results in prompting the user for credentials
		 * @param domain
		 * @param description
		 * @return
		 */
		Credentials authenticate(String domain, String description, CredentialType type);
	}

	/**
	 * Requests authentication for the given domain.
	 * If the user enters wrong information, he will be prompted again
	 * a) either until the Credentials are correct
	 * b) or the user cancels the dialog.
	 * This may cause blocking of this thread  since a auth dialog may be shown to the user.
	 *
	 *
	 * @param domain The domain id
	 * @param description A request description for the user
	 * @param type The type
	 * @param useKeyStore Shall remembered keys being used to authenticate?
	 * @return On success, returns the valid Credentials.
	 */
    Credentials requestAuthentication(String domain, String description, CredentialType type, ICredentialsChecker checker, boolean useKeyStore);

	/**
	 * Requests new credentials, the user has to confirm the entered credentials
	 * @param description
	 * @param type
	 * @return
	 */
    Credentials requestNewCredentials(String description, CredentialType type);

	/**
	 * Requests credentials without checking them and without using a key-store
	 * @param description
	 * @param type
	 * @return
	 */
    Credentials requestCredentials(String description, CredentialType type);



	/**
	 * Registers the given Credentials provider
	 * @param authProvider
	 */
    void register(ICredentialsProvider authProvider);


}
