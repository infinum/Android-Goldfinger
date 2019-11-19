package co.infinum.goldfinger.crypto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Base interface for encryption and decryption. The interface is used internally after
 * user successfully authenticates and the library has to encrypt or decrypt given value.
 *
 * @param <T> Object used to encrypt or decrypt data, one of:
 *            {@link java.security.Signature}, {@link javax.crypto.Cipher} or {@link javax.crypto.Mac}
 */
interface Crypter<T> {

    /**
     * Encrypt value with given crypter.
     *
     * @param crypter unlocked crypter that can be used to encrypt or sign the value.
     * @param value   plain text value that should be encrypted.
     * @return encrypted value or null if encryption fails.
     */
    @Nullable
    String encrypt(@NonNull T crypter, @NonNull String value);

    /**
     * Decrypt encrypted value with given crypter.
     *
     * @param crypter unlocked crypter that can be used to decrypt or validate the value.
     * @param value   encrypted value that should be decrypted.
     * @return decrypted value or null if decryption fails.
     */
    @Nullable
    String decrypt(@NonNull T crypter, @NonNull String value);
}
