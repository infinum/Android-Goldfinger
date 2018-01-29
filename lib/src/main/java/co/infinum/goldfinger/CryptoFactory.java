package co.infinum.goldfinger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Base64;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public interface CryptoFactory {

    /**
     * Create CryptoObject for authentication call. Return null if invalid.
     */
    @Nullable
    FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(String keyName);

    /**
     * Create CryptoObject for encryption call. Return null if invalid.
     */
    @Nullable
    FingerprintManagerCompat.CryptoObject createEncryptionCryptoObject(String keyName);

    /**
     * Create CryptoObject for decryption call. Return null if invalid.
     */
    @Nullable
    FingerprintManagerCompat.CryptoObject createDecryptionCryptoObject(String keyName);

    @RequiresApi(Build.VERSION_CODES.M)
    class Default implements CryptoFactory {

        private static final String KEY_SHARED_PREFS = "<Goldfinger IV>";
        private static final String KEY_KEYSTORE = "AndroidKeyStore";

        private Logger logger;
        private KeyStore keyStore;
        private KeyGenerator keyGenerator;
        private final SharedPreferences sharedPrefs;

        Default(Context context, Logger logger) {
            this.logger = logger;
            this.sharedPrefs = context.getSharedPreferences(KEY_SHARED_PREFS, Context.MODE_PRIVATE);
            try {
                keyStore = KeyStore.getInstance(KEY_KEYSTORE);
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_KEYSTORE);
            } catch (Exception e) {
                logger.log(e);
            }
        }

        @Nullable
        @Override
        public FingerprintManagerCompat.CryptoObject createAuthenticationCryptoObject(String keyName) {
            return createCryptoObject(keyName, Mode.AUTHENTICATION);
        }

        @Nullable
        @Override
        public FingerprintManagerCompat.CryptoObject createEncryptionCryptoObject(String keyName) {
            return createCryptoObject(keyName, Mode.ENCRYPTION);
        }

        @Nullable
        @Override
        public FingerprintManagerCompat.CryptoObject createDecryptionCryptoObject(String keyName) {
            return createCryptoObject(keyName, Mode.DECRYPTION);
        }

        private FingerprintManagerCompat.CryptoObject createCryptoObject(String keyName, Mode mode) {
            if (keyStore == null || keyGenerator == null) {
                return null;
            }

            try {
                Key key = (mode == Mode.DECRYPTION) ? loadKey(keyName) : createKey(keyName);
                Cipher cipher = createCipher(keyName, mode, key);
                return new FingerprintManagerCompat.CryptoObject(cipher);
            } catch (Exception e) {
                logger.log(e);
                return null;
            }
        }

        private Key createKey(String keyName) throws Exception {
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

        private Key loadKey(String keyName) throws Exception {
            keyStore.load(null);
            return keyStore.getKey(keyName, null);
        }

        private Cipher createCipher(String keyName, Mode mode, Key key) throws Exception {
            String transformation = String.format("%s/%s/%s",
                    KeyProperties.KEY_ALGORITHM_AES,
                    KeyProperties.BLOCK_MODE_CBC,
                    KeyProperties.ENCRYPTION_PADDING_PKCS7);
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

        private byte[] loadIv(String keyName) {
            return Base64.decode(sharedPrefs.getString(keyName, ""), Base64.DEFAULT);
        }

        private void saveIv(String keyName, byte[] iv) {
            sharedPrefs.edit().putString(keyName, Base64.encodeToString(iv, Base64.DEFAULT)).apply();
        }
    }
}
