package co.infinum.goldfinger;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Interface implements crypto operations on given value when using
 * {@link Goldfinger#decrypt(GoldfingerParams, GoldfingerCallback)} or
 * {@link Goldfinger#encrypt(GoldfingerParams, GoldfingerCallback)} methods.
 */
public interface CryptographyHandler {

    /**
     * Encrypt value with unlocked cryptoObject. Return null if encryption fails.
     */
    @Nullable
    String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull CryptographyData cryptographyData);

    /**
     * Decrypt value with unlocked cryptoObject. Return null if decryption fails.
     */
    @Nullable
    String decrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull CryptographyData cryptographyData);

    @SuppressWarnings("ConstantConditions")
    class Default implements CryptographyHandler {

        @Nullable
        @Override
        public String decrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull CryptographyData cryptographyData) {
            try {
                byte[] decodedBytes = Base64.decode(cryptographyData.value(), Base64.DEFAULT);
                return new String(cryptoObject.getCipher().doFinal(decodedBytes));
            } catch (Exception e) {
                log(e);
                return null;
            }
        }

        @Nullable
        @Override
        public String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull CryptographyData cryptographyData) {
            try {
                byte[] encryptedBytes = cryptoObject.getCipher().doFinal(cryptographyData.value().getBytes());
                return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            } catch (Exception e) {
                log(e);
                return null;
            }
        }
    }
}
