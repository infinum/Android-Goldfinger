package co.infinum.goldfinger.crypto;

import androidx.annotation.Nullable;

/**
 * Base interface used to create the crypter object that will be used to
 * create {@link android.hardware.biometrics.BiometricPrompt.CryptoObject}.
 *
 * @param <T> one of: {@link javax.crypto.Cipher}, {@link javax.crypto.Mac} or {@link java.security.Signature}.
 */
interface Factory<T> {

    /**
     * Create crypter which will be used when encrypting the value.
     *
     * @param key used to store IV, Key, etc. so that it can be restored.
     * @return created crypter or null if error happens.
     */
    @Nullable
    T createEncryptionCrypter(String key);

    /**
     * Create crypter which will be used when decrypting the value.
     *
     * @param key used to restore IV, Key, etc.
     * @return created crypter or null if error happens.
     */
    @Nullable
    T createDecryptionCrypter(String key);
}
