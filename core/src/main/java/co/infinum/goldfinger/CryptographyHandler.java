package co.infinum.goldfinger;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Interface implements crypto operations on given value.
 *
 * @see Goldfinger.PromptParams.Builder#encrypt
 * @see Goldfinger.PromptParams.Builder#decrypt
 */
public interface CryptographyHandler {

    /**
     * Encrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     * Return null if encryption fails.
     */
    @Nullable
    String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value);

    /**
     * Decrypt value with unlocked {@link BiometricPrompt.CryptoObject}.
     * Return null if decryption fails.
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
