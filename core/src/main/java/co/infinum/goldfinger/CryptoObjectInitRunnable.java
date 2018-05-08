package co.infinum.goldfinger;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

class CryptoObjectInitRunnable implements Runnable {

    private final static Handler mainHandler = new Handler(Looper.getMainLooper());

    private final AsyncCryptoFactory.Callback callback;
    private final CryptoFactory cryptoFactory;
    private final String keyName;
    private final Mode mode;

    CryptoObjectInitRunnable(CryptoFactory cryptoFactory, String keyName, Mode mode, AsyncCryptoFactory.Callback callback) {
        this.cryptoFactory = cryptoFactory;
        this.keyName = keyName;
        this.mode = mode;
        this.callback = callback;
    }

    @Override
    public void run() {
        final FingerprintManagerCompat.CryptoObject cryptoObject;
        switch (mode) {
            case AUTHENTICATION:
                cryptoObject = cryptoFactory.createAuthenticationCryptoObject(keyName);
                break;
            case DECRYPTION:
                cryptoObject = cryptoFactory.createDecryptionCryptoObject(keyName);
                break;
            case ENCRYPTION:
                cryptoObject = cryptoFactory.createEncryptionCryptoObject(keyName);
                break;
            default:
                cryptoObject = null;
                break;
        }

        if (!callback.canceled) {
            /* Return callback back to main thread as this is executed in the background */
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onCryptoObjectCreated(cryptoObject);
                }
            });
        }
    }
}
