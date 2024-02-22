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
public class SharedPrefs {

    private static SharedPreferences PREFS;
    private static CipherCrypter CRYPTER;
    private static CipherFactory FACTORY;

    private static final String BIOMETRIC_PIN = "BIOMETRIC_PIN";
    private static final String PIN = "PIN";
    private static final String IS_RX = "IS_RX";
    private static final String AUTHENTICATORS = "AUTHENTICATORS";
    private static final String STRONG_AUTHENTICATOR = "STRONG_AUTHENTICATOR";
    private static final String WEAK_AUTHENTICATOR = "WEAK_AUTHENTICATOR";
    private static final String DEVICE_CREDENTIALS_AUTHENTICATOR = "DEVICE_CREDENTIALS_AUTHENTICATOR";

    public static void clear() {
        PREFS.edit().clear().apply();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public static void clearBiometricPin() {
        PREFS.edit().remove(BIOMETRIC_PIN).apply();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public static String getBiometricPin() {
        return PREFS.getString(BIOMETRIC_PIN, null);
    }

    @Nullable
    @RequiresApi(Build.VERSION_CODES.M)
    public static String getPin() {
        String encryptedPin = PREFS.getString(PIN, "");
        if ("".equals(encryptedPin)) {
            return "";
        }

        Cipher cipher = FACTORY.createDecryptionCrypter(PIN);
        if (cipher == null) {
            return "";
        }

        return CRYPTER.decrypt(cipher, encryptedPin);
    }

    /**
     * Please do not do this in production.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public static void init(Context context) {
        PREFS = context.getSharedPreferences("My awesome app prefs", Context.MODE_PRIVATE);
        CRYPTER = new Base64CipherCrypter();
        FACTORY = new UnlockedAesCipherFactory(context);
    }

    public static boolean isRxExample() {
        return PREFS.getBoolean(IS_RX, false);
    }

    public static void setBiometricPin(String encryptedPin) {
        PREFS.edit().putString(BIOMETRIC_PIN, encryptedPin).apply();
    }

    public static void setPin(String pin) {
        Cipher cipher = FACTORY.createEncryptionCrypter(PIN);
        if (cipher == null) {
            return;
        }

        String encryptedPin = CRYPTER.encrypt(cipher, pin);
        PREFS.edit().putString(PIN, encryptedPin).apply();
    }

    public static void setRxExample(boolean isRxExample) {
        PREFS.edit().putBoolean(IS_RX, isRxExample).commit();
    }

    public static void setStrongAuth(boolean strongAuth) {
        PREFS.edit().putBoolean(STRONG_AUTHENTICATOR, strongAuth).commit();
    }

    public static boolean getStrongAuth() {
        return PREFS.getBoolean(STRONG_AUTHENTICATOR, false);
    }

    public static void setWeakAuth(boolean weakAuth) {
        PREFS.edit().putBoolean(WEAK_AUTHENTICATOR, weakAuth).commit();
    }

    public static boolean getWeakAuth() {
        return PREFS.getBoolean(WEAK_AUTHENTICATOR, false);
    }

    public static void setDeviceCredentialsAuth(boolean deviceCredentialsAuth) {
        PREFS.edit().putBoolean(DEVICE_CREDENTIALS_AUTHENTICATOR, deviceCredentialsAuth).commit();
    }

    public static boolean getDeviceCredentialsAuth() {
        return PREFS.getBoolean(DEVICE_CREDENTIALS_AUTHENTICATOR, false);
    }

    public static void setAuthenticators(int authenticators) {
        PREFS.edit().putInt(AUTHENTICATORS, authenticators).commit();
    }

    public static int getAuthenticators() {
        return PREFS.getInt(AUTHENTICATORS, 0);
    }
}
