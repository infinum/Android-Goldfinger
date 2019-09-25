package co.infinum.goldfinger;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Extended default callback.
 * Tracks if the authentication is still active and handles multiple
 * edge cases that are not expected by the user.
 */
@SuppressWarnings({"ConstantConditions", "NullableProblems"})
class BiometricCallback extends BiometricPrompt.AuthenticationCallback {

    @NonNull private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    boolean isAuthenticationActive = true;

    @NonNull private final Goldfinger.Callback callback;
    @NonNull private final CryptographyHandler cryptographyHandler;
    @NonNull private final Mode mode;
    @Nullable private final String value;

    BiometricCallback(
        @NonNull CryptographyHandler cryptographyHandler,
        @NonNull Mode mode,
        @Nullable String value,
        @NonNull Goldfinger.Callback callback
    ) {
        this.cryptographyHandler = cryptographyHandler;
        this.mode = mode;
        this.value = value;
        this.callback = callback;
    }

    @Override
    public void onAuthenticationError(int errMsgId, final CharSequence errString) {
        if (!isAuthenticationActive) {
            return;
        }

        isAuthenticationActive = false;
        final Goldfinger.Reason reason = EnumConverter.errorToReason(errMsgId);
        log("onAuthenticationError [%s]", reason);
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                Goldfinger.Result result = new Goldfinger.Result(
                    Goldfinger.Type.ERROR,
                    reason,
                    null,
                    errString != null ? errString.toString() : null
                );
                callback.onResult(result);
            }
        });
    }

    @Override
    public void onAuthenticationFailed() {
        if (!isAuthenticationActive) {
            return;
        }

        log("onAuthenticationFailed [%s]", Goldfinger.Reason.AUTHENTICATION_FAIL);
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                Goldfinger.Result result = new Goldfinger.Result(
                    Goldfinger.Type.INFO,
                    Goldfinger.Reason.AUTHENTICATION_FAIL
                );
                callback.onResult(result);
            }
        });
    }

    @Override
    public void onAuthenticationSucceeded(@NonNull final BiometricPrompt.AuthenticationResult result) {
        if (!isAuthenticationActive) {
            return;
        }

        isAuthenticationActive = false;
        log("onAuthenticationSucceeded");
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mode == Mode.AUTHENTICATION) {
                    Goldfinger.Result goldfingerResult = new Goldfinger.Result(
                        Goldfinger.Type.SUCCESS,
                        Goldfinger.Reason.AUTHENTICATION_SUCCESS
                    );
                    callback.onResult(goldfingerResult);
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
            cipheredValue = cryptographyHandler.decrypt(cryptoObject, value);
        } else {
            cipheredValue = cryptographyHandler.encrypt(cryptoObject, value);
        }

        if (cipheredValue != null) {
            log("Ciphered [%s] => [%s]", value, cipheredValue);
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
