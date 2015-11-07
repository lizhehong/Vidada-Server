package com.elderbyte.vidada.cache.crypto;

import archimedes.core.crypto.IByteBufferEncryption;
import archimedes.core.crypto.KeyCurruptedException;
import archimedes.core.crypto.KeyPad;
import archimedes.core.crypto.XORByteCrypter;
import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.security.CredentialType;
import archimedes.core.security.Credentials;
import archimedes.core.util.Debug;
import com.elderbyte.vidada.security.CredentialUtil;
import com.elderbyte.vidada.security.ICredentialManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.net.URISyntaxException;

public class CryptedCacheUtil {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    transient private static final Logger logger = LogManager.getLogger(CryptedCacheUtil.class.getName());


    transient private final static int KEYPAD_SIZE = 20;

	transient private final static IByteBufferEncryption keyCrypter = new XORByteCrypter();
	transient private final static String KeyFileName = "cache.keypad";
	transient private final static String EncryptedKeyFileName = "cache.encrypted";

    /***************************************************************************
     *                                                                         *
     * Public methods                                                          *
     *                                                                         *
     **************************************************************************/

	/**
	 * Gets the encryption keypad for the given folder
	 * @param root
	 * @return
	 */
	public static byte[] getEncryptionKeyPad(DirectoryLocation root, ICredentialManager credentialManager) {

		byte[] keyPad = null;

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);
		} catch (URISyntaxException e1) {
            logger.error(e1);
			return keyPad;
		}

		if(keyFile.exists())
		{
            logger.info("Reading KeyPad from file: " + keyFile);
			keyPad = readKeyPad(keyFile);
		}else if(enckeyFile.exists()){
            logger.info("Reading encrypted KeyPad from file: " + enckeyFile);
			keyPad = readEncryptedKeyPad(enckeyFile, credentialManager);
		}else{
			// no file found
			// time to generate a new key
			keyPad = generateNewKeyPad(keyFile);

		}
		return keyPad;
	}

	private static byte[] readKeyPad(ResourceLocation keyFile){
		byte[] keyPad = null;
		try {
			keyPad = keyFile.readAllBytes();
			KeyPad.checkKey(keyPad);
		} catch (KeyCurruptedException e) {
			logger.error(e);
		} catch (IOException e) {
            logger.error(e);
		}
		return keyPad;
	}

	/*
	private static byte[] readEncryptedKeyPad(ResourceLocation enckeyFile, Credentials credentials){
		byte[] keyPad = null;
		try {

			final byte[] encryptedPad = enckeyFile.readAllBytes();
			keyPad = decryptPad(encryptedPad, credentials);

			try {
				// should always pass this check
				// since the creditals were checked previously
				KeyPad.checkKey(keyPad);
			} catch (KeyCurruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return keyPad;
	}
	 */


	/**
	 * Encrypt the key pad with the given password
	 *
	 *
	 * @param root
	 * @param credentials
	 */
	public static void encryptWithPassword(DirectoryLocation root, Credentials credentials){

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);

			if(enckeyFile.exists())
				throw new NotSupportedException("Already encrypted!");

			if(!keyFile.exists())
				throw new NotSupportedException("Keyfile not found! " + keyFile);

			try {
				byte[] pad = keyFile.readAllBytes();

				byte[] encPad = encryptPad(pad, credentials);

				enckeyFile.writeAllBytes(encPad);

				keyFile.delete();
			} catch (IOException e) {
                logger.error(e);
			}

		} catch (URISyntaxException e1) {
            logger.error(e1);
		}
	}



	/**
	 * Removes the encryption from the key pad with the given password
	 *
	 * @param root
	 * @param oldPass
	 */
	public static void removeEncryption(DirectoryLocation root, Credentials oldPass){

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);

			if(enckeyFile.exists()){

				try {
					byte[] encryptedPad = enckeyFile.readAllBytes();
					byte[] key = decryptPad(encryptedPad, oldPass);
					keyFile.delete();
					keyFile.writeAllBytes(key);
					enckeyFile.delete();
				} catch (IOException e) {
                    logger.error(e);
				}

			}else
				throw new NotSupportedException("Already decrypted!");
		} catch (URISyntaxException e1) {
            logger.error(e1);
		}
	}

	public static byte[] encryptPad(byte[] pad, Credentials credentials){
		return keyCrypter.enCrypt(pad, credentials.getUserSecret());
	}

	public static byte[] decryptPad(byte[] encryptedPad, Credentials credentials){
		return keyCrypter.deCrypt(encryptedPad, credentials.getUserSecret());
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private static byte[] readEncryptedKeyPad(ResourceLocation enckeyFile, ICredentialManager credentialManager){
        byte[] keyPad = null;

        try {

            final byte[] encryptedPad = enckeyFile.readAllBytes();
            logger.debug("CRYPTO PAD: " + Debug.toString(encryptedPad) + " len: " + encryptedPad.length );

            String domain = CredentialUtil.toDomain("vidada.cache", enckeyFile.toString());

            Credentials validCredentials = credentialManager.requestAuthentication(
                    domain,
                    "Enter password for cache " + enckeyFile.toString(),
                    CredentialType.PasswordOnly,
                    new ICredentialManager.ICredentialsChecker() {
                        @Override
                        public boolean check(Credentials credentials) {

                            byte[] currentKeyPad = decryptPad(encryptedPad, credentials);

                            logger.trace("decrypted pad: " + Debug.toString(currentKeyPad) + " len: " + currentKeyPad.length );
                            logger.trace("encrypted pad: " + Debug.toString(encryptedPad) + " len: " + encryptedPad.length );

                            return KeyPad.validate(currentKeyPad);
                        }
                    }  ,true);

            if(validCredentials != null){
                logger.debug("ReadEncryptedKeyPad: Valid Credentials: " + validCredentials);

                keyPad = decryptPad(encryptedPad, validCredentials);

               logger.debug("decrypted pad: " + Debug.toString(keyPad) + " len: " + keyPad.length );
               logger.debug("encrypted pad: " + Debug.toString(encryptedPad) + " len: " + encryptedPad.length );

                try {
                    // should always pass this check
                    // since the creditals were checked previously
                    KeyPad.checkKey(keyPad);
                } catch (KeyCurruptedException e) {
                    e.printStackTrace();
                    keyPad = null;
                }
            }else {
                logger.warn("ReadEncryptedKeyPad: User could not provide correct credentials.");
            }

        } catch (IOException e) {
            logger.error(e);
        }

        return keyPad;
    }


    private static byte[] generateNewKeyPad(ResourceLocation keyFile){
        byte[] keyPad = KeyPad.generateKey(KEYPAD_SIZE);
        try {
            keyFile.writeAllBytes(keyPad);
            logger.info("Generated new cache encryption key.");
        } catch (IOException e) {
            logger.error(e);
        }
        return keyPad;
    }


}
