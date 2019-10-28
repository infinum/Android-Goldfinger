package co.infinum.goldfinger;

import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

class CryptoProxy {

    private final @Nullable CipherCryptoHandler cipherCryptoHandler;
    private final @Nullable MacCryptoHandler macCryptoHandler;
    private final @Nullable SignatureCryptoHandler signatureCryptoHandler;

    CryptoProxy(
        @Nullable CipherCryptoHandler cipherCryptoHandler,
        @Nullable MacCryptoHandler macCryptoHandler,
        @Nullable SignatureCryptoHandler signatureCryptoHandler
    ) {
        this.cipherCryptoHandler = cipherCryptoHandler;
        this.macCryptoHandler = macCryptoHandler;
        this.signatureCryptoHandler = signatureCryptoHandler;
    }

    @Nullable
    public String decrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {
        Cipher cipher = cryptoObject.getCipher();
        if (cipher != null && cipherCryptoHandler != null) {
            return cipherCryptoHandler.decrypt(cipher, value);
        }

        Mac mac = cryptoObject.getMac();
        if (mac != null && macCryptoHandler != null) {
            return macCryptoHandler.decrypt(mac, value);
        }

        Signature signature = cryptoObject.getSignature();
        if (signature != null && signatureCryptoHandler != null) {
            return signatureCryptoHandler.decrypt(signature, value);
        }

        return null;
    }

    @Nullable
    public String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {
        Cipher cipher = cryptoObject.getCipher();
        if (cipher != null && cipherCryptoHandler != null) {
            return cipherCryptoHandler.encrypt(cipher, value);
        }

        Mac mac = cryptoObject.getMac();
        if (mac != null && macCryptoHandler != null) {
            return macCryptoHandler.encrypt(mac, value);
        }

        Signature signature = cryptoObject.getSignature();
        if (signature != null && signatureCryptoHandler != null) {
            return signatureCryptoHandler.encrypt(signature, value);
        }

        return null;
    }
}
