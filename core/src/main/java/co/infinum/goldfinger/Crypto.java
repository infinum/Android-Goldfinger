package co.infinum.goldfinger;

import android.util.Base64;

import javax.crypto.Cipher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Interface implements crypto operations on given value when using
 * {@link Goldfinger#decrypt(String, String, Goldfinger.Callback)} or
 * {@link Goldfinger#encrypt(String, String, Goldfinger.Callback)} methods.
 */
public interface Crypto {

    /**
     * Encrypt value with unlocked cryptoObject. Return null if encryption fails.
     */
    @Nullable
    String encrypt(@NonNull FingerprintManagerCompat.CryptoObject cryptoObject, @NonNull String value);

    /**
     * Decrypt value with unlocked cryptoObject. Return null if decryption fails.
     */
    @Nullable
    String decrypt(@NonNull FingerprintManagerCompat.CryptoObject cryptoObject, @NonNull String value);

    class Default implements Crypto {

        @Nullable
        @Override
        public String decrypt(@NonNull FingerprintManagerCompat.CryptoObject cryptoObject, @NonNull String value) {
            Cipher cipher = cryptoObject.getCipher();
            if (cipher == null) {
                log("decrypt Cipher = [NULL]");
                return null;
            }

            try {
                byte[] decodedBytes = Base64.decode(value, Base64.DEFAULT);
                return new String(cipher.doFinal(decodedBytes));
            } catch (Exception e) {
                log(e);
                return null;
            }
        }

        @Nullable
        @Override
        public String encrypt(@NonNull FingerprintManagerCompat.CryptoObject cryptoObject, @NonNull String value) {
            Cipher cipher = cryptoObject.getCipher();
            if (cipher == null) {
                log("encrypt Cipher = [NULL]");
                return null;
            }

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
