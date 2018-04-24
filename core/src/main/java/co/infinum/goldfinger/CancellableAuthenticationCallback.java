package co.infinum.goldfinger;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import static co.infinum.goldfinger.LogUtils.log;

class CancellableAuthenticationCallback extends FingerprintManagerCompat.AuthenticationCallback {

    private static final long IGNORE_CANCEL_MS = 100;

    final CancellationSignal cancellationSignal;

    private final Goldfinger.Callback callback;
    private final Clock clock;
    private final Crypto crypto;
    private final long initializationTimeMs;
    private final Mode mode;
    private final String value;

    CancellableAuthenticationCallback(Crypto crypto, Clock clock, Mode mode, String value, Goldfinger.Callback callback) {
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
        Error error = Error.fromFingerprintError(errMsgId);
        if (shouldReactToError(error)) {
            onError(error);
        }
    }

    @Override
    public void onAuthenticationFailed() {
        if (!cancellationSignal.isCanceled()) {
            onError(Error.FAILURE);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Error error = Error.fromFingerprintHelp(helpMsgId);
        if (!cancellationSignal.isCanceled()) {
            onError(error);
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        if (!cancellationSignal.isCanceled()) {
            log("Successful authentication");
            if (mode == Mode.AUTHENTICATION) {
                callback.onSuccess("");
            } else {
                cipherValue(result.getCryptoObject(), value);
            }
        }
    }

    void cancel() {
        if (!cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }

    private void cipherValue(FingerprintManagerCompat.CryptoObject cryptoObject, String value) {
        String cipheredValue = null;
        switch (mode) {
            case DECRYPTION:
                cipheredValue = crypto.decrypt(cryptoObject, value);
                break;
            case ENCRYPTION:
                cipheredValue = crypto.encrypt(cryptoObject, value);
                break;
        }

        if (cipheredValue != null) {
            log("Ciphered [%s] => [%s]", value, cipheredValue);
            callback.onSuccess(cipheredValue);
        } else {
            Error error = (mode == Mode.DECRYPTION) ? Error.DECRYPTION_FAILED : Error.ENCRYPTION_FAILED;
            onError(error);
        }
    }

    private void onError(Error error) {
        log("Error [%s]", error);
        callback.onError(error);
    }

    private boolean shouldReactToError(Error error) {
        return !cancellationSignal.isCanceled()
            && (error != Error.CANCELED || clock.isBeforeNow(initializationTimeMs + IGNORE_CANCEL_MS));
    }
}
