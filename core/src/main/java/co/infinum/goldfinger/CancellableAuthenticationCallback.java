package co.infinum.goldfinger;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Extended {@link FingerprintManagerCompat.AuthenticationCallback} because default implementation
 * does not support cancel functionality.
 */
class CancellableAuthenticationCallback extends FingerprintManagerCompat.AuthenticationCallback {

    @NonNull final CancellationSignal cancellationSignal;
    boolean isAuthenticationActive = true;

    @NonNull private final Goldfinger.Callback callback;
    @NonNull private final Crypto crypto;
    @NonNull private final Mode mode;
    @NonNull private final String value;

    CancellableAuthenticationCallback(
        @NonNull Crypto crypto,
        @NonNull Mode mode,
        @NonNull String value,
        @NonNull Goldfinger.Callback callback
    ) {
        this.crypto = crypto;
        this.mode = mode;
        this.value = value;
        this.callback = callback;
        this.cancellationSignal = new CancellationSignal();
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Goldfinger.Reason reason = EnumConverter.errorToReason(errMsgId);
        if (!isAuthenticationActive) {
            return;
        }

        isAuthenticationActive = false;
        log("onAuthenticationError [%s]", reason);
        callback.onResult(new Goldfinger.Result(
            Goldfinger.Type.ERROR,
            reason,
            null,
            errString != null ? errString.toString() : null
        ));
    }

    @Override
    public void onAuthenticationFailed() {
        if (!isAuthenticationActive) {
            return;
        }

        log("onAuthenticationFailed [%s]", Goldfinger.Reason.AUTHENTICATION_FAIL);
        callback.onResult(new Goldfinger.Result(
            Goldfinger.Type.INFO,
            Goldfinger.Reason.AUTHENTICATION_FAIL
        ));
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        if (!isAuthenticationActive) {
            return;
        }

        Goldfinger.Reason reason = EnumConverter.helpToReason(helpMsgId);
        log("onAuthenticationHelp [%s]", reason);
        callback.onResult(new Goldfinger.Result(
            Goldfinger.Type.INFO,
            reason,
            null,
            helpString != null ? helpString.toString() : null
        ));
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        if (!isAuthenticationActive) {
            return;
        }

        isAuthenticationActive = false;
        log("onAuthenticationSucceeded");
        if (mode == Mode.AUTHENTICATION) {
            callback.onResult(new Goldfinger.Result(
                Goldfinger.Type.SUCCESS,
                Goldfinger.Reason.AUTHENTICATION_SUCCESS
            ));
        } else {
            cipherValue(result.getCryptoObject(), value);
        }
    }

    /**
     * Cancel Goldfinger authentication.
     *
     * Native authentication will invoke {@link #onAuthenticationError(int, CharSequence)}
     * but the error will be ignored because the user knowingly canceled the authentication.
     */
    void cancel() {
        if (isAuthenticationActive) {
            isAuthenticationActive = false;
            cancellationSignal.cancel();
        }
    }

    /**
     * Cipher the value with unlocked {@link FingerprintManagerCompat.CryptoObject}.
     *
     * @param cryptoObject unlocked {@link FingerprintManagerCompat.CryptoObject} that is ready to use
     * @param value        String passed by the user that must be ciphered.
     */
    private void cipherValue(FingerprintManagerCompat.CryptoObject cryptoObject, String value) {
        String cipheredValue = (mode == Mode.DECRYPTION) ? crypto.decrypt(cryptoObject, value) : crypto.encrypt(cryptoObject, value);

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
