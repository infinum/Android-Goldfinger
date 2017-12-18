package co.infinum.goldfinger;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

class CancellableAuthenticationCallback extends FingerprintManagerCompat.AuthenticationCallback {

    private final Crypto crypto;
    private final Mode mode;
    private final String value;
    private final Goldfinger.Callback callback;
    final CancellationSignal cancellationSignal = new CancellationSignal();

    CancellableAuthenticationCallback(Crypto crypto, Mode mode, String value, Goldfinger.Callback callback) {
        this.crypto = crypto;
        this.mode = mode;
        this.value = value;
        this.callback = callback;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!cancellationSignal.isCanceled()) {
            callback.onError(Error.fromId(errMsgId));
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        if (!cancellationSignal.isCanceled()) {
            callback.onWarning(Warning.fromId(helpMsgId));
        }
    }

    @Override
    public void onAuthenticationFailed() {
        if (!cancellationSignal.isCanceled()) {
            callback.onWarning(Warning.FAILURE);
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        if (!cancellationSignal.isCanceled()) {
            String cryptedValue = "";
            switch (mode) {
                case DECRYPTION:
                    cryptedValue = crypto.decrypt(result.getCryptoObject(), value);
                    break;
                case ENCRYPTION:
                    cryptedValue = crypto.encrypt(result.getCryptoObject(), value);
                    break;
            }

            if (cryptedValue != null) {
                callback.onSuccess(cryptedValue);
            } else {
                Error error = (mode == Mode.DECRYPTION) ? Error.DECRYPTION_FAILED : Error.ENCRYPTION_FAILED;
                callback.onError(error);
            }
        }
    }

    void cancel() {
        if (!cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }
}
