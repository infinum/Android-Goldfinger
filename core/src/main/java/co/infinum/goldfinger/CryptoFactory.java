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
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Interface used for {@link androidx.core.hardware.fingerprint.FingerprintManagerCompat.CryptoObject}
 * creation. Has separate method for {@link Goldfinger#authenticate(Goldfinger.Callback)},
 * {@link Goldfinger#decrypt(String, String, Goldfinger.Callback)} and
 * {@link Goldfinger#encrypt(String, String, Goldfinger.Callback)}
 */
public interface CryptoFactory {

    /**
     * Create CryptoObject for authentication call. Return null if invalid.
     */
    @Nullable
    FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(@NonNull String keyName);

    /**
     * Create CryptoObject for encryption call. Return null if invalid.
     */
    @Nullable
    FingerprintManagerCompat.CryptoObject createEncryptionCryptoObject(@NonNull String keyName);

    /**
     * Create CryptoObject for decryption call. Return null if invalid.
     */
    @Nullable
    FingerprintManagerCompat.CryptoObject createDecryptionCryptoObject(@NonNull String keyName);

    @RequiresApi(Build.VERSION_CODES.M)
    class Default implements CryptoFactory {

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
        public FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(@NonNull String keyName) {
            return createCryptoObject(keyName, Mode.AUTHENTICATION);
        }

        @Nullable
        @Override
        public FingerprintManagerCompat.CryptoObject createDecryptionCryptoObject(@NonNull String keyName) {
            return createCryptoObject(keyName, Mode.DECRYPTION);
        }

        @Nullable
        @Override
        public FingerprintManagerCompat.CryptoObject createEncryptionCryptoObject(@NonNull String keyName) {
            return createCryptoObject(keyName, Mode.ENCRYPTION);
        }

        @NonNull
        private Cipher createCipher(@NonNull String keyName, @NonNull Mode mode, @Nullable Key key) throws Exception {
            String transformation = String.format(
                "%s/%s/%s",
                KeyProperties.KEY_ALGORITHM_AES,
                KeyProperties.BLOCK_MODE_CBC,
                KeyProperties.ENCRYPTION_PADDING_PKCS7
            );
            Cipher cipher = Cipher.getInstance(transformation);
            if (mode == Mode.DECRYPTION) {
                byte[] iv = loadIv(keyName);
                cipher.init(mode.cipherMode(), key, new IvParameterSpec(iv));
            } else {
                cipher.init(mode.cipherMode(), key);
                saveIv(keyName, cipher.getIV());
            }
            return cipher;
        }

        @Nullable
        private FingerprintManagerCompat.CryptoObject createCryptoObject(@NonNull String keyName, @NonNull Mode mode) {
            if (keyStore == null || keyGenerator == null) {
                return null;
            }

            try {
                Key key = (mode == Mode.DECRYPTION) ? loadKey(keyName) : createKey(keyName);
                Cipher cipher = createCipher(keyName, mode, key);
                return new FingerprintManagerCompat.CryptoObject(cipher);
            } catch (Exception e) {
                log(e);
                return null;
            }
        }

        @Nullable
        private Key createKey(@NonNull String keyName) throws Exception {
            KeyGenParameterSpec.Builder keyGenParamsBuilder =
                new KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                keyGenParamsBuilder.setInvalidatedByBiometricEnrollment(true);
            }
            keyGenerator.init(keyGenParamsBuilder.build());
            keyGenerator.generateKey();
            return loadKey(keyName);
        }

        @NonNull
        private byte[] loadIv(@NonNull String keyName) {
            return Base64.decode(sharedPrefs.getString(keyName, ""), Base64.DEFAULT);
        }

        @Nullable
        private Key loadKey(@NonNull String keyName) throws Exception {
            keyStore.load(null);
            return keyStore.getKey(keyName, null);
        }

        private void saveIv(@NonNull String keyName, @Nullable byte[] iv) {
            sharedPrefs.edit().putString(keyName, Base64.encodeToString(iv, Base64.DEFAULT)).apply();
        }
    }
}
