package com.elderbyte.vidada.domain.images.cache.crypto;

/**
 * Encryption Keypad provider for the CryptedImageFileCache
 *
 * @author IsNull
 *
 */
public interface ICacheKeyProvider {

	/**
	 * Get the encryption key for the given cache
	 * @param cache
	 * @return
	 */
	public abstract byte[] getEncryptionKeyPad(CryptedImageFileCache cache);
}
