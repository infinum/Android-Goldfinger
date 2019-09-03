package co.infinum.goldfinger;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Extended default callback.
 * Tracks if the authentication is still active and
 */
class BiometricCallback extends BiometricPrompt.AuthenticationCallback {

    boolean isAuthenticationActive = true;

    @NonNull private final Goldfinger.Callback callback;
    @NonNull private final CryptographyHandler cryptographyHandler;
    @NonNull private final Mode mode;
    @NonNull private final CryptographyData cryptographyData;
    @NonNull private final Handler mainHandler = new Handler(Looper.getMainLooper());

    BiometricCallback(
        @NonNull CryptographyHandler cryptographyHandler,
        @NonNull Mode mode,
        @NonNull CryptographyData cryptographyData,
        @NonNull Goldfinger.Callback callback
    ) {
        this.cryptographyHandler = cryptographyHandler;
        this.mode = mode;
        this.cryptographyData = cryptographyData;
        this.callback = callback;
    }

    @Override
    public void onAuthenticationError(int errMsgId, @NonNull final CharSequence errString) {
        final Goldfinger.Reason reason = EnumConverter.errorToReason(errMsgId);
        if (!isAuthenticationActive) {
            return;
        }

        isAuthenticationActive = false;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                log("onAuthenticationError [%s]", reason);
                callback.onResult(new Goldfinger.Result(
                    Goldfinger.Type.ERROR,
                    reason,
                    null,
                    errString.toString()
                ));
            }
        });
    }

    @Override
    public void onAuthenticationFailed() {
        if (!isAuthenticationActive) {
            return;
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                log("onAuthenticationFailed [%s]", Goldfinger.Reason.AUTHENTICATION_FAIL);
                callback.onResult(new Goldfinger.Result(
                    Goldfinger.Type.INFO,
                    Goldfinger.Reason.AUTHENTICATION_FAIL
                ));
            }
        });
    }

    @Override
    public void onAuthenticationSucceeded(@NonNull final BiometricPrompt.AuthenticationResult result) {
        if (!isAuthenticationActive) {
            return;
        }

        isAuthenticationActive = false;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                log("onAuthenticationSucceeded");
                if (mode == Mode.AUTHENTICATION) {
                    callback.onResult(new Goldfinger.Result(
                        Goldfinger.Type.SUCCESS,
                        Goldfinger.Reason.AUTHENTICATION_SUCCESS
                    ));
                } else {
                    cipherValue(result.getCryptoObject());
                }
            }
        });
    }

    /**
     * Cancel Goldfinger authentication.
     * <p>
     * Native authentication will invoke {@link #onAuthenticationError}
     * but the error will be ignored because the user knowingly canceled the authentication.
     */
    void cancel() {
        isAuthenticationActive = false;
    }

    /**
     * Cipher the value with unlocked {@link BiometricPrompt.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link BiometricPrompt.CryptoObject} that is ready to use
     */
    private void cipherValue(BiometricPrompt.CryptoObject cryptoObject) {
        String cipheredValue;
        if (mode == Mode.DECRYPTION) {
            cipheredValue = cryptographyHandler.decrypt(cryptoObject, cryptographyData);
        } else {
            cipheredValue = cryptographyHandler.encrypt(cryptoObject, cryptographyData);
        }

        if (cipheredValue != null) {
            log("Ciphered [%s] => [%s]", cryptographyData.value(), cipheredValue);
            callback.onResult(new Goldfinger.Result(
                Goldfinger.Type.SUCCESS,
                Goldfinger.Reason.AUTHENTICATION_SUCCESS,
                cipheredValue,
                null
            ));
        } else {
            Exception e = (mode == Mode.DECRYPTION) ? new DecryptionException() : new EncryptionException();
            callback.onError(e);
        }
    }
}
