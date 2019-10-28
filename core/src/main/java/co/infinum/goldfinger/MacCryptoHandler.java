package co.infinum.goldfinger;

import javax.crypto.Mac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

/**
 * Interface implements crypto operations on given value.
 *
 * @see Goldfinger#encrypt
 * @see Goldfinger#decrypt
 */
public interface MacCryptoHandler {

    /**
     * Encrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link BiometricPrompt.CryptoObject} can be used for encryption
     * @param value        plain text value that should be encrypted
     * @return encrypted value or null if encryption fails
     */
    @Nullable
    String encrypt(@NonNull Mac mac, @NonNull String value);

    /**
     * Encrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link BiometricPrompt.CryptoObject} can be used for decryption
     * @param value        previously encrypted value that should be decrypted
     * @return decrypted value or null if encryption fails
     */
    @Nullable
    String decrypt(@NonNull Mac mac, @NonNull String value);
}

