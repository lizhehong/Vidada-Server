package com.elderbyte.vidada.security;

import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.security.CredentialType;
import archimedes.core.security.Credentials;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


@Deprecated
@Service
public class CredentialManager implements ICredentialManager {


    private static final Logger logger = LogManager.getLogger(CredentialManager.class.getName());



    @Override
	public Credentials requestAuthentication(String domain, String description, CredentialType type, ICredentialsChecker checker, boolean useKeyStore) {

        logger.debug("requestAuthentication for " + domain);

		Credentials credentials = null;
		if(useKeyStore){
			throw new NotImplementedException(); // TODO
			/*
			try {
				credentials = creditalsFor(domain);
			} catch (AuthenticationRequieredException e) {
				e.printStackTrace();
			}*/
		}

		if(credentials == null){
			// we have no saved credentials, we need to ask the user
			if(authProvider == null)
				throw new NotSupportedException("No authProvider was registered. -> requestAuthentication");

			// as for correct authentication until success or abort
			while (true) {

                logger.debug("CredentialManager: Asking user for credentials...");
				credentials = authProvider.authenticate(domain, description, type);

				if(credentials != null){
					if(checker.check(credentials))
					{
                        logger.debug("CredentialManager: User entered correct credentials.");

						if(credentials.isRemember()){
							/*
							try {
								System.out.println("CredentialManager: Storing credentials for " + domain);
								storeCredentials(domain, credentials);
							} catch (AuthenticationRequieredException e) {
								e.printStackTrace();
							}*/
							throw new NotImplementedException(); // TODO
						}

						// Credentials were correct
						break;
					}else{
                        logger.debug("CredentialManager: User entered wrong credentials.");
					}
				}else {
                    logger.debug("CredentialManager: User Canceled entering credentials.");
					// User canceled
					break;
				}

			}
		}
		return credentials;
	}

	transient private ICredentialsProvider authProvider;
	@Override
	public void register(ICredentialsProvider authProvider) {
		this.authProvider = authProvider;
	}


	@Override
	public Credentials requestCredentials(String description,
			CredentialType type) {
		if(authProvider == null)
			throw new NotSupportedException("No authProvider was registered. -> requestAuthentication");

		return authProvider.authenticate(null, description, type);
	}

	@Override
	public Credentials requestNewCredentials(String description,
			CredentialType type) {
		if(authProvider == null)
			throw new NotSupportedException("No authProvider was registered. -> requestAuthentication");

		Credentials credentials = requestCredentials(description, type);

		if(credentials != null){
			Credentials credentialsConf = requestCredentials("Please confirm your credentials.", type);
			if(!credentials.equals(credentialsConf)){
                logger.debug("Credential confirmation failed.");
				credentials = null;
			}
		}
		return credentials;
	}


}
