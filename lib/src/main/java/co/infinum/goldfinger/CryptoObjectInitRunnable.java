package co.infinum.goldfinger;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

class CryptoObjectInitRunnable implements Runnable {

    private final CryptoFactory cryptoFactory;
    private final String keyName;
    private final Mode mode;
    private final AsyncCryptoFactory.Callback callback;

    CryptoObjectInitRunnable(CryptoFactory cryptoFactory, String keyName, Mode mode, AsyncCryptoFactory.Callback callback) {
        this.cryptoFactory = cryptoFactory;
        this.keyName = keyName;
        this.mode = mode;
        this.callback = callback;
    }

    @Override
    public void run() {
        FingerprintManagerCompat.CryptoObject cryptoObject = null;
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
        }

        if (!callback.canceled) {
            callback.onCryptoObjectCreated(cryptoObject);
        }
    }
}
