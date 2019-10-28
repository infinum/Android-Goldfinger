package co.infinum.goldfinger;

import android.util.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Interface implements crypto operations on given value.
 *
 * @see Goldfinger#encrypt
 * @see Goldfinger#decrypt
 */
public interface CipherCryptoHandler {

    /**
     * Encrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link BiometricPrompt.CryptoObject} can be used for encryption
     * @param value        plain text value that should be encrypted
     * @return encrypted value or null if encryption fails
     */
    @Nullable
    String encrypt(@NonNull Cipher cipher, @NonNull String value);

    /**
     * Encrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link BiometricPrompt.CryptoObject} can be used for decryption
     * @param value        previously encrypted value that should be decrypted
     * @return decrypted value or null if encryption fails
     */
    @Nullable
    String decrypt(@NonNull Cipher cipher, @NonNull String value);

    @SuppressWarnings("ConstantConditions")
    class Default implements CipherCryptoHandler {

        @Nullable
        @Override
        public String decrypt(@NonNull Cipher cipher, @NonNull String value) {
            try {
                byte[] decodedBytes = Base64.decode(value, Base64.NO_WRAP);
                return new String(cipher.doFinal(decodedBytes));
            } catch (Exception e) {
                log(e);
                return null;
            }
        }

        @Nullable
        @Override
        public String encrypt(@NonNull Cipher cipher, @NonNull String value) {
            try {
                byte[] encryptedBytes = cipher.doFinal(value.getBytes("utf-8"));
                return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
            } catch (Exception e) {
                log(e);
                return null;
            }
        }
    }
}

