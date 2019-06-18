package co.infinum.goldfinger;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import static co.infinum.goldfinger.LogUtils.log;

class CancellableAuthenticationCallback extends FingerprintManagerCompat.AuthenticationCallback {

    private static final long IGNORE_CANCEL_MS = 100;

    @NonNull final CancellationSignal cancellationSignal;

    @NonNull private final Goldfinger.Callback callback;
    @NonNull private final Clock clock;
    @NonNull private final Crypto crypto;
    @NonNull private final Mode mode;
    @NonNull private final String value;
    private final long initializationTimeMs;

    CancellableAuthenticationCallback(
        @NonNull Crypto crypto,
        @NonNull Clock clock,
        @NonNull Mode mode,
        @NonNull String value,
        @NonNull Goldfinger.Callback callback
    ) {
        this.crypto = crypto;
        this.mode = mode;
        this.value = value;
        this.callback = callback;
        this.clock = clock;
        this.initializationTimeMs = clock.currentTimeMs();
        this.cancellationSignal = new CancellationSignal();
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Goldfinger.Reason reason = EnumConverter.errorToReason(errMsgId);
        if (shouldReactToError(reason)) {
            log("onAuthenticationError [%s]", reason);
            callback.onResult(new Goldfinger.Result(
                Goldfinger.Type.ERROR,
                reason,
                null,
                errString != null ? errString.toString() : null
            ));
        }
    }

    @Override
    public void onAuthenticationFailed() {
        if (cancellationSignal.isCanceled()) {
            return;
        }

        log("onAuthenticationFailed [%s]");
        callback.onResult(new Goldfinger.Result(
            Goldfinger.Type.INFO,
            Goldfinger.Reason.AUTHENTICATION_FAIL
        ));
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        if (cancellationSignal.isCanceled()) {
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
        if (cancellationSignal.isCanceled()) {
            return;
        }

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

    void cancel() {
        if (!cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }

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

    private boolean shouldReactToError(Goldfinger.Reason reason) {
        return !cancellationSignal.isCanceled()
            && (reason != Goldfinger.Reason.CANCELED || clock.isBeforeNow(initializationTimeMs + IGNORE_CANCEL_MS));
    }
}
