package co.infinum.example;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import javax.crypto.Cipher;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import co.infinum.goldfinger.crypto.CipherCrypter;
import co.infinum.goldfinger.crypto.CipherFactory;
import co.infinum.goldfinger.crypto.impl.Base64CipherCrypter;
import co.infinum.goldfinger.crypto.impl.UnlockedAesCipherFactory;

/**
 * Encrypted Shared prefs wrapper which encrypts and decrypts PIN
 * automatically using Goldfinger's exposed API.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class SharedPrefs {

    private static SharedPreferences PREFS;
    private static CipherCrypter CRYPTER;
    private static CipherFactory FACTORY;

    public static void clear() {
        PREFS.edit().clear().apply();
    }

    public static void clearFingerprintPin() {
        PREFS.edit().remove("fp_pin").apply();
    }

    public static String getFingerprintPin() {
        return PREFS.getString("fp_pin", null);
    }

    @Nullable
    public static String getPin() {
        String encryptedPin = PREFS.getString("pin", "");
        if ("".equals(encryptedPin)) {
            return "";
        }

        Cipher cipher = FACTORY.createDecryptionCrypter("pin");
        if (cipher == null) {
            return "";
        }

        return CRYPTER.decrypt(cipher, encryptedPin);
    }

    /**
     * Please do not do this in production.
     */
    public static void init(Context context) {
        PREFS = context.getSharedPreferences("My awesome app prefs", Context.MODE_PRIVATE);
        CRYPTER = new Base64CipherCrypter();
        FACTORY = new UnlockedAesCipherFactory(context);
    }

    public static boolean isRxExample() {
        return PREFS.getBoolean("isRx", false);
    }

    public static void setFingerprintPin(String encryptedPin) {
        PREFS.edit().putString("fp_pin", encryptedPin).apply();
    }

    public static void setPin(String pin) {
        Cipher cipher = FACTORY.createEncryptionCrypter("pin");
        if (cipher == null) {
            return;
        }

        String encryptedPin = CRYPTER.encrypt(cipher, pin);
        PREFS.edit().putString("pin", encryptedPin).apply();
    }

    public static void setRxExample(boolean isRxExample) {
        PREFS.edit().putBoolean("isRx", isRxExample).commit();
    }
}
