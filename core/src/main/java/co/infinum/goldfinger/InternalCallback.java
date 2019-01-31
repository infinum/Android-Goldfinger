package co.infinum.goldfinger;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

class InternalCallback extends BiometricPrompt.AuthenticationCallback {

    private final GoldfingerCallback callback;
    private final CryptographyData cryptographyData;
    private final CryptographyHandler cryptographyHandler;
    private final Mode mode;

    InternalCallback(
        @NonNull CryptographyHandler cryptographyHandler,
        @NonNull Mode mode,
        @NonNull CryptographyData cryptographyData,
        @NonNull GoldfingerCallback callback
    ) {
        this.cryptographyHandler = cryptographyHandler;
        this.mode = mode;
        this.cryptographyData = cryptographyData;
        this.callback = callback;
    }

    @Override
    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
        onError(Error.fromBiometricError(errorCode));
    }

    @Override
    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
        if (this.mode == Mode.AUTHENTICATION) {
            this.callback.onSuccess("");
        } else if (result.getCryptoObject() != null) {
            String cipheredValue = cipherValue(result.getCryptoObject());
            if (cipheredValue != null && !cipheredValue.isEmpty()) {
                this.callback.onSuccess(cipheredValue);
            } else {
                Error error = (mode == Mode.DECRYPTION) ? Error.DECRYPTION_FAILED : Error.ENCRYPTION_FAILED;
                onError(error);
            }
            log("Ciphered value= [%s] => [%s]", this.cryptographyData.getValue(), cipheredValue);
        } else {
            onError(Error.UNKNOWN);
        }
    }

    private String cipherValue(@NonNull BiometricPrompt.CryptoObject cryptoObject) {
        switch (mode) {
            case DECRYPTION:
                return cryptographyHandler.decrypt(cryptoObject, cryptographyData);
            case ENCRYPTION:
                return cryptographyHandler.encrypt(cryptoObject, cryptographyData);
            default:
                return null;
        }
    }

    private void onError(Error error) {
        log("Error [%s]", error);
        callback.onError(error);
    }
}
