package co.infinum.goldfinger;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

class InternalCallback extends BiometricPrompt.AuthenticationCallback {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

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
    public void onAuthenticationFailed() {
        log("Fail");
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                callback.onFail();
            }
        });
    }

    @Override
    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
        if (this.mode == Mode.AUTHENTICATION) {
            onSuccess(new Goldfinger.Result(Goldfinger.Reason.AUTHENTICATION, null));
        } else if (result.getCryptoObject() != null) {
            String cipheredValue = cipherValue(result.getCryptoObject());
            if (cipheredValue != null && !cipheredValue.isEmpty()) {
                Goldfinger.Reason reason = Mode.DECRYPTION == mode ? Goldfinger.Reason.DECRYPTION : Goldfinger.Reason.ENCRYPTION;
                onSuccess(new Goldfinger.Result(reason, cipheredValue));
            } else {
                Error error = Mode.DECRYPTION == mode ? Error.DECRYPTION_FAILED : Error.ENCRYPTION_FAILED;
                onError(error);
            }
            log("Ciphered value= [%s] => [%s]", this.cryptographyData.value(), cipheredValue);
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

    private void onError(final Error error) {
        log("Error [%s]", error);
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(error);
            }
        });
    }

    private void onSuccess(final Goldfinger.Result result) {
        log("Success [%s]", result.value());
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(result);
            }
        });
    }
}
