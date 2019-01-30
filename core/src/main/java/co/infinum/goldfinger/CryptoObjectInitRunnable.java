package co.infinum.goldfinger;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

class CryptoObjectInitRunnable implements Runnable {

    private final static Handler mainHandler = new Handler(Looper.getMainLooper());

    private final AsyncCryptoObjectFactory.Callback callback;
    private final CryptoObjectFactory cryptoFactory;
    @NonNull private final CryptographyData cryptographyData;
    private final Mode mode;

    CryptoObjectInitRunnable(
        @NonNull CryptoObjectFactory cryptoFactory,
        @NonNull CryptographyData cryptographyData,
        @NonNull Mode mode,
        @NonNull AsyncCryptoObjectFactory.Callback callback
    ) {
        this.cryptoFactory = cryptoFactory;
        this.cryptographyData = cryptographyData;
        this.mode = mode;
        this.callback = callback;
    }

    @Override
    public void run() {
        final BiometricPrompt.CryptoObject cryptoObject;
        switch (mode) {
            case DECRYPTION:
                cryptoObject = cryptoFactory.createDecryptionCryptoObject(cryptographyData);
                break;
            case ENCRYPTION:
                cryptoObject = cryptoFactory.createEncryptionCryptoObject(cryptographyData);
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
