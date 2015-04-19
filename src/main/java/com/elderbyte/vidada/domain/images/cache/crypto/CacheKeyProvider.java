package com.elderbyte.vidada.domain.images.cache.crypto;


import com.elderbyte.vidada.domain.security.ICredentialManager;

/**
 * The cache encryption
 * --------------------
 * As a general remark, the cached images are always encrypted.
 * The difference between the two states is, that in DECRYPTED
 * state, the encryption key-pad is freely available in the root folder
 * while in ENCRYPTED state, the key-pad is itself encrypted with the users
 * password. The key-pad has a role similar to a master password.
 *
 *
 * DECRYPTED STATE
 *
 * In the DECRYPTED state, the encryption keypad is saved
 * as "cache.key" in the root folder of the cache. The
 * keypad is generated randomly the first time a cache is
 * used.
 *
 * This basically means that the contents are always
 * encrypted, but the key is freely available in the root.
 *
 *
 *
 * ENCRYPTED STATE
 *
 * When the cache contents are protected, the "cache.key"
 * gets AES encrypted with the users password and saved as
 * "cache.encrypted". The not encrypted "cache.key" is
 * off course removed.
 *
 * @author IsNull
 *
 */
public class CacheKeyProvider implements ICacheKeyProvider{

	transient private final ICredentialManager credentialManager;

	public CacheKeyProvider(ICredentialManager credentialManager){
		this.credentialManager = credentialManager;
	}


	@Override
	public synchronized byte[] getEncryptionKeyPad(CryptedImageFileCache cache) {
		return CryptedCacheUtil.getEncryptionKeyPad(
				cache.getCacheRoot(),
				credentialManager);
	}


}
