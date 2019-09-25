package co.infinum.goldfinger;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

/**
 * Custom runnable that creates CryptoObject.
 * Used for asynchronous creation.
 */
class CryptoObjectInitRunnable implements Runnable {

    private final static Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    @NonNull private final AsyncCryptoObjectFactory.Callback callback;
    @NonNull private final CryptoObjectFactory cryptoFactory;
    @NonNull private final Mode mode;
    @NonNull private final String key;

    CryptoObjectInitRunnable(
        @NonNull CryptoObjectFactory cryptoFactory,
        @NonNull Mode mode,
        @NonNull String key,
        @NonNull AsyncCryptoObjectFactory.Callback callback
    ) {
        this.cryptoFactory = cryptoFactory;
        this.mode = mode;
        this.key = key;
        this.callback = callback;
    }

    @Override
    public void run() {
        final BiometricPrompt.CryptoObject cryptoObject;
        switch (mode) {
            case DECRYPTION:
                cryptoObject = cryptoFactory.createDecryptionCryptoObject(key);
                break;
            case ENCRYPTION:
                cryptoObject = cryptoFactory.createEncryptionCryptoObject(key);
                break;
            default:
                cryptoObject = null;
                break;
        }

        if (!callback.canceled) {
            /* Return callback back to main thread as this is executed in the background */
            MAIN_HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    callback.onCryptoObjectCreated(cryptoObject);
                }
            });
        }
    }
}
