package co.infinum.goldfinger;

import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

class CryptoObjectFactory {

    @Nullable private final CipherFactory cipherFactory;
    @Nullable private final MacFactory macFactory;
    @Nullable private final SignatureFactory signatureFactory;

    CryptoObjectFactory(
        @Nullable CipherFactory cipherFactory,
        @Nullable MacFactory macFactory,
        @Nullable SignatureFactory signatureFactory
    ) {
        this.cipherFactory = cipherFactory;
        this.macFactory = macFactory;
        this.signatureFactory = signatureFactory;
    }

    @Nullable
    BiometricPrompt.CryptoObject createCryptoObject(@NonNull String key, @NonNull Mode mode) {
        if (cipherFactory != null) {
            return createCipherCryptoObject(key, mode);
        } else if (macFactory != null) {
            return createMacCryptoObject(key, mode);
        } else if (signatureFactory != null) {
            return createSignatureCryptoObject(key, mode);
        } else {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private BiometricPrompt.CryptoObject createCipherCryptoObject(String key, Mode mode) {
        Cipher cipher = Mode.ENCRYPTION == mode ? cipherFactory.createEncryptionCipher(key) : cipherFactory.createDecryptionCipher(key);
        return cipher != null ? new BiometricPrompt.CryptoObject(cipher) : null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private BiometricPrompt.CryptoObject createMacCryptoObject(@NonNull String key, @NonNull Mode mode) {
        Mac mac = Mode.ENCRYPTION == mode ? macFactory.createEncryptionMac(key) : macFactory.createDecryptionMac(key);
        return mac != null ? new BiometricPrompt.CryptoObject(mac) : null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private BiometricPrompt.CryptoObject createSignatureCryptoObject(@NonNull String key, @NonNull Mode mode) {
        Signature signature =
            Mode.ENCRYPTION == mode ? signatureFactory.createEncryptionSignature(key) : signatureFactory.createDecryptionSignature(key);
        return signature != null ? new BiometricPrompt.CryptoObject(signature) : null;
    }
}
