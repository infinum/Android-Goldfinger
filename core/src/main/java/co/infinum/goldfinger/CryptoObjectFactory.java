package co.infinum.goldfinger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Interface used for {@link BiometricPrompt.CryptoObject} creation.
 * Only used for encryption and decryption operations.
 *
 * @see Goldfinger.PromptParams.Builder#encrypt
 * @see Goldfinger.PromptParams.Builder#decrypt
 */
public interface CryptoObjectFactory {

    /**
     * Create CryptoObject for encryption call. Return null if invalid.
     */
    @Nullable
    BiometricPrompt.CryptoObject createEncryptionCryptoObject(@NonNull String key);

    /**
     * Create CryptoObject for decryption call. Return null if invalid.
     */
    @Nullable
    BiometricPrompt.CryptoObject createDecryptionCryptoObject(@NonNull String key);

    @RequiresApi(Build.VERSION_CODES.M)
    class Default implements CryptoObjectFactory {

        private static final String KEY_KEYSTORE = "AndroidKeyStore";
        private static final String KEY_SHARED_PREFS = "<Goldfinger IV>";

        private KeyGenerator keyGenerator;
        private KeyStore keyStore;
        private final SharedPreferences sharedPrefs;

        Default(@NonNull Context context) {
            this.sharedPrefs = context.getSharedPreferences(KEY_SHARED_PREFS, Context.MODE_PRIVATE);
            try {
                keyStore = KeyStore.getInstance(KEY_KEYSTORE);
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_KEYSTORE);
            } catch (Exception e) {
                log(e);
            }
        }

        @Nullable
        @Override
        public BiometricPrompt.CryptoObject createDecryptionCryptoObject(@NonNull String key) {
            return createCryptoObject(key, Mode.DECRYPTION);
        }

        @Nullable
        @Override
        public BiometricPrompt.CryptoObject createEncryptionCryptoObject(@NonNull String key) {
            return createCryptoObject(key, Mode.ENCRYPTION);
        }

        /**
         * Create {@link Cipher} for encryption or decryption.
         * If encryption is used, it also creates new IV and saves it to Shared preferences.
         * If decryption is used, it loads existing IV from Shared preferences.
         */
        @NonNull
        private Cipher createCipher(@NonNull String key, @NonNull Mode mode, @Nullable Key secureKey) throws Exception {
            String transformation = String.format(
                "%s/%s/%s",
                KeyProperties.KEY_ALGORITHM_AES,
                KeyProperties.BLOCK_MODE_CBC,
                KeyProperties.ENCRYPTION_PADDING_PKCS7
            );
            Cipher cipher = Cipher.getInstance(transformation);
            if (mode == Mode.DECRYPTION) {
                byte[] iv = loadIv(key);
                cipher.init(mode.cipherMode(), secureKey, new IvParameterSpec(iv));
            } else {
                cipher.init(mode.cipherMode(), secureKey);
                saveIv(key, cipher.getIV());
            }
            return cipher;
        }

        /**
         * Create new {@link BiometricPrompt.CryptoObject} for encryption or decryption.
         * Handle all cases gracefully and return null if anything bad happens.
         */
        @Nullable
        private BiometricPrompt.CryptoObject createCryptoObject(String key, Mode mode) {
            if (keyStore == null || keyGenerator == null) {
                return null;
            }

            try {
                Key secureKey = (mode == Mode.DECRYPTION) ? loadKey(key) : createKey(key);
                Cipher cipher = createCipher(key, mode, secureKey);
                return new BiometricPrompt.CryptoObject(cipher);
            } catch (Exception e) {
                log(e);
                return null;
            }
        }

        /**
         * Generate new {@link Key} for encryption purposes.
         */
        @Nullable
        private Key createKey(@NonNull String key) throws Exception {
            KeyGenParameterSpec.Builder keyGenParamsBuilder =
                new KeyGenParameterSpec.Builder(key, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                keyGenParamsBuilder.setInvalidatedByBiometricEnrollment(true);
            }
            keyGenerator.init(keyGenParamsBuilder.build());
            keyGenerator.generateKey();
            return loadKey(key);
        }

        /**
         * Load IV from Shared preferences. Decode from Base64.
         */
        @NonNull
        private byte[] loadIv(@NonNull String key) {
            return Base64.decode(sharedPrefs.getString(key, ""), Base64.DEFAULT);
        }

        /**
         * Load {@link Key} from {@link KeyStore}.
         */
        @Nullable
        private Key loadKey(@NonNull String key) throws Exception {
            keyStore.load(null);
            return keyStore.getKey(key, null);
        }

        /**
         * Save IV to Shared preferences. Before saving encode it to Base64.
         */
        private void saveIv(@NonNull String key, @Nullable byte[] iv) {
            sharedPrefs.edit().putString(key, Base64.encodeToString(iv, Base64.DEFAULT)).apply();
        }
    }
}
