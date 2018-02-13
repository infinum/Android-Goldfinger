package co.infinum.goldfinger;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

class CancellableAuthenticationCallback extends FingerprintManagerCompat.AuthenticationCallback {

    private static final long IGNORE_CANCEL_MS = 100;

    private final Crypto crypto;
    private final Logger logger;
    private final Mode mode;
    private final String value;
    private final Goldfinger.Callback callback;
    private final long initializationTime;
    private final Clock clock;
    final CancellationSignal cancellationSignal;

    CancellableAuthenticationCallback(Crypto crypto, Logger logger, Clock clock, Mode mode, String value, Goldfinger.Callback callback) {
        this.crypto = crypto;
        this.logger = logger;
        this.mode = mode;
        this.value = value;
        this.callback = callback;
        this.clock = clock;
        this.initializationTime = clock.currentTimeMs();
        this.cancellationSignal = new CancellationSignal();
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Error error = Error.fromId(errMsgId);
        if (shouldReactToError(error)) {
            logger.log("Error [%s]", error);
            callback.onError(error);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Warning warning = Warning.fromId(helpMsgId);
        if (!cancellationSignal.isCanceled()) {
            logger.log("Warning [%s]", warning);
            callback.onWarning(warning);
        }
    }

    @Override
    public void onAuthenticationFailed() {
        if (!cancellationSignal.isCanceled()) {
            logger.log("Warning [%s]", Warning.FAILURE);
            callback.onWarning(Warning.FAILURE);
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        if (!cancellationSignal.isCanceled()) {
            logger.log("Successful authentication");
            if (mode == Mode.AUTHENTICATION) {
                callback.onSuccess("");
            } else {
                cipherValue(result.getCryptoObject(), value);
            }
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
            logger.log("Ciphered [%s] => [%s]", value, cipheredValue);
            callback.onSuccess(cipheredValue);
        } else {
            Error error = (mode == Mode.DECRYPTION) ? Error.DECRYPTION_FAILED : Error.ENCRYPTION_FAILED;
            logger.log("Error [%s]", error);
            callback.onError(error);
        }
    }

    void cancel() {
        if (!cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }

    private boolean shouldReactToError(Error error) {
        return !cancellationSignal.isCanceled()
                && (error != Error.CANCELED || clock.isBeforeNow(initializationTime + IGNORE_CANCEL_MS));
    }
}
