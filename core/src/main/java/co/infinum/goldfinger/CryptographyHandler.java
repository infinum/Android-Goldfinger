package co.infinum.goldfinger;

import android.util.Base64;

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
public interface CryptographyHandler {

    /**
     * Encrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link BiometricPrompt.CryptoObject} can be used for encryption
     * @param value        plain text value that should be encrypted
     * @return encrypted value or null if encryption fails
     */
    @Nullable
    String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value);

    /**
     * Encrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link BiometricPrompt.CryptoObject} can be used for decryption
     * @param value        previously encrypted value that should be decrypted
     * @return decrypted value or null if encryption fails
     */
    @Nullable
    String decrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value);

    @SuppressWarnings("ConstantConditions")
    class Default implements CryptographyHandler {

        @Nullable
        @Override
        public String decrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {
            try {
                byte[] decodedBytes = Base64.decode(value, Base64.DEFAULT);
                return new String(cryptoObject.getCipher().doFinal(decodedBytes));
            } catch (Exception e) {
                log(e);
                return null;
            }
        }

        @Nullable
        @Override
        public String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {
            try {
                byte[] encryptedBytes = cryptoObject.getCipher().doFinal(value.getBytes());
                return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            } catch (Exception e) {
                log(e);
                return null;
            }
        }
    }
}
