package co.infinum.goldfinger.crypto.impl;

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
import co.infinum.goldfinger.crypto.CipherFactory;

/**
 * AES Cipher implementation. By default the given Cipher is created with
 * Key which requires user authentication.
 * This implementation is used by default if other Factory is not provided.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class AesCipherFactory implements CipherFactory {

    private static final String CIPHER_TRANSFORMATION = String.format(
        "%s/%s/%s",
        KeyProperties.KEY_ALGORITHM_AES,
        KeyProperties.BLOCK_MODE_CBC,
        KeyProperties.ENCRYPTION_PADDING_PKCS7
    );
    private static final String KEY_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_SHARED_PREFS = "<Goldfinger IV>";
    private KeyGenerator keyGenerator;
    private KeyStore keyStore;
    private final SharedPreferences sharedPrefs;

    public AesCipherFactory(@NonNull Context context) {
        this.sharedPrefs = context.getSharedPreferences(KEY_SHARED_PREFS, Context.MODE_PRIVATE);
        try {
            keyStore = KeyStore.getInstance(KEY_KEYSTORE);
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_KEYSTORE);
        } catch (Exception ignored) {
            /* Gracefully handle exception later when create method is invoked. */
        }
    }

    @Nullable
    @Override
    public Cipher createDecryptionCrypter(String key) {
        if (keyStore == null || keyGenerator == null) {
            return null;
        }

        try {
            Key secureKey = loadKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            byte[] iv = loadIv(key);
            cipher.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(iv));
            return cipher;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    @Override
    public Cipher createEncryptionCrypter(@NonNull String key) {
        if (keyStore == null || keyGenerator == null) {
            return null;
        }

        try {
            Key secureKey = createKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secureKey);
            saveIv(key, cipher.getIV());
            return cipher;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Simple method created to be easily extendable and create cipher which
     * does not require user authentication.
     */
    protected boolean isUserAuthRequired() {
        return true;
    }

    /**
     * Create secure key used to create Cipher.
     *
     * @param key name of the keystore.
     * @return created key, or null if something weird happens.
     * @throws Exception if anything fails, it is handled gracefully.
     */
    @Nullable
    private Key createKey(@NonNull String key) throws Exception {
        KeyGenParameterSpec.Builder keyGenParamsBuilder =
            new KeyGenParameterSpec.Builder(key, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(isUserAuthRequired());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            keyGenParamsBuilder.setInvalidatedByBiometricEnrollment(isUserAuthRequired());
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
     *
     * @param key name of the {@link Key} to load.
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
