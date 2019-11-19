package co.infinum.goldfinger;

import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import co.infinum.goldfinger.crypto.CipherCrypter;
import co.infinum.goldfinger.crypto.MacCrypter;
import co.infinum.goldfinger.crypto.SignatureCrypter;

/**
 * Internal wrapper around different crypters to have this logic
 * in one place hidden from the rest of the code.
 */
class CrypterProxy {

    private final @Nullable CipherCrypter cipherCrypter;
    private final @Nullable MacCrypter macCrypter;
    private final @Nullable SignatureCrypter signatureCrypter;

    CrypterProxy(
        @Nullable CipherCrypter cipherCrypter,
        @Nullable MacCrypter macCrypter,
        @Nullable SignatureCrypter signatureCrypter
    ) {
        this.cipherCrypter = cipherCrypter;
        this.macCrypter = macCrypter;
        this.signatureCrypter = signatureCrypter;
    }

    @Nullable
    public String decrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {
        Cipher cipher = cryptoObject.getCipher();
        if (cipher != null && cipherCrypter != null) {
            return cipherCrypter.decrypt(cipher, value);
        }

        Mac mac = cryptoObject.getMac();
        if (mac != null && macCrypter != null) {
            return macCrypter.decrypt(mac, value);
        }

        Signature signature = cryptoObject.getSignature();
        if (signature != null && signatureCrypter != null) {
            return signatureCrypter.decrypt(signature, value);
        }

        return null;
    }

    @Nullable
    public String encrypt(@NonNull BiometricPrompt.CryptoObject cryptoObject, @NonNull String value) {
        Cipher cipher = cryptoObject.getCipher();
        if (cipher != null && cipherCrypter != null) {
            return cipherCrypter.encrypt(cipher, value);
        }

        Mac mac = cryptoObject.getMac();
        if (mac != null && macCrypter != null) {
            return macCrypter.encrypt(mac, value);
        }

        Signature signature = cryptoObject.getSignature();
        if (signature != null && signatureCrypter != null) {
            return signatureCrypter.encrypt(signature, value);
        }

        return null;
    }
}
