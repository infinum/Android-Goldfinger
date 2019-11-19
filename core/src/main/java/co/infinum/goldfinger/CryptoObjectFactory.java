package co.infinum.goldfinger;

import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import co.infinum.goldfinger.crypto.CipherFactory;
import co.infinum.goldfinger.crypto.MacFactory;
import co.infinum.goldfinger.crypto.SignatureFactory;

/**
 * Wrapper around different factories. It decides which factory should be used
 * when creating CryptoObject.
 */
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
        Cipher cipher = Mode.ENCRYPTION == mode ? cipherFactory.createEncryptionCrypter(key) : cipherFactory.createDecryptionCrypter(key);
        return cipher != null ? new BiometricPrompt.CryptoObject(cipher) : null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private BiometricPrompt.CryptoObject createMacCryptoObject(@NonNull String key, @NonNull Mode mode) {
        Mac mac = Mode.ENCRYPTION == mode ? macFactory.createEncryptionCrypter(key) : macFactory.createDecryptionCrypter(key);
        return mac != null ? new BiometricPrompt.CryptoObject(mac) : null;
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private BiometricPrompt.CryptoObject createSignatureCryptoObject(@NonNull String key, @NonNull Mode mode) {
        Signature signature =
            Mode.ENCRYPTION == mode ? signatureFactory.createEncryptionCrypter(key) : signatureFactory.createDecryptionCrypter(key);
        return signature != null ? new BiometricPrompt.CryptoObject(signature) : null;
    }
}
