package co.infinum.goldfinger;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

class CancellableAuthenticationCallback extends FingerprintManagerCompat.AuthenticationCallback {

    private final Crypto crypto;
    private final Logger logger;
    private final Mode mode;
    private final String value;
    private final Goldfinger.Callback callback;
    final CancellationSignal cancellationSignal = new CancellationSignal();

    CancellableAuthenticationCallback(Crypto crypto, Logger logger, Mode mode, String value, Goldfinger.Callback callback) {
        this.crypto = crypto;
        this.logger = logger;
        this.mode = mode;
        this.value = value;
        this.callback = callback;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!cancellationSignal.isCanceled()) {
            Error error = Error.fromId(errMsgId);
            logger.log("Error [%s]", error);
            callback.onError(error);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        if (!cancellationSignal.isCanceled()) {
            Warning warning = Warning.fromId(helpMsgId);
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
}
