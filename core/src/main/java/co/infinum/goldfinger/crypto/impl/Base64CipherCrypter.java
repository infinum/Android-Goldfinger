package co.infinum.goldfinger.crypto.impl;

import android.os.Build;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import co.infinum.goldfinger.crypto.CipherCrypter;

/**
 * Implementation uses unlocked cipher to encrypt or decrypt the data.
 * Used by default if other Crypter implementation is not used.
 *
 * @see CipherCrypter
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Base64CipherCrypter implements CipherCrypter {

    @Nullable
    @Override
    public String decrypt(@NonNull Cipher cipher, @NonNull String value) {
        try {
            byte[] decodedBytes = Base64.decode(value, Base64.NO_WRAP);
            return new String(cipher.doFinal(decodedBytes));
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    @Override
    public String encrypt(@NonNull Cipher cipher, @NonNull String value) {
        try {
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }
}
