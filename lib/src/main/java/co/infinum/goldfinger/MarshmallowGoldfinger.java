package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

@RequiresApi(Build.VERSION_CODES.M)
class MarshmallowGoldfinger implements Goldfinger {

    private static final String KEY_AUTH_MODE = "<Goldfinger authentication mode>";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final CryptoFactory cryptoFactory;
    private final Crypto crypto;
    private final FingerprintManagerCompat fingerprintManagerCompat;
    private final Logger logger;
    private CancellableAuthenticationCallback cancellableAuthenticationCallback;

    MarshmallowGoldfinger(Context context, CryptoFactory cryptoFactory, Crypto crypto, Logger logger) {
        this.cryptoFactory = cryptoFactory;
        this.crypto = crypto;
        this.fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        this.logger = logger;
    }

    @Override
    public boolean hasFingerprintHardware() {
        return fingerprintManagerCompat.isHardwareDetected();
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        return fingerprintManagerCompat.hasEnrolledFingerprints();
    }

    @Override
    public void authenticate(Callback callback) {
        startFingerprintAuthentication(KEY_AUTH_MODE, "", Mode.AUTHENTICATION, callback);
    }

    @Override
    public void decrypt(String keyName, String value, Callback callback) {
        startFingerprintAuthentication(keyName, value, Mode.DECRYPTION, callback);
    }

    @Override
    public void encrypt(String keyName, String value, Callback callback) {
        startFingerprintAuthentication(keyName, value, Mode.ENCRYPTION, callback);
    }

    private void startFingerprintAuthentication(String keyName, String value, Mode mode, Callback callback) {
        cancel();

        logger.log("Creating CryptoObject");
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

        if (cryptoObject == null) {
            logger.log("Failed to create CryptoObject");
            callback.onError(Error.CRYPTO_OBJECT_INIT);
            return;
        }

        logger.log("Starting authentication [keyName=%s; value=%s]", keyName, value);
        cancellableAuthenticationCallback = new CancellableAuthenticationCallback(crypto, logger, Clock.instance(), mode, value, callback);
        fingerprintManagerCompat.authenticate(cryptoObject,
                0,
                cancellableAuthenticationCallback.cancellationSignal,
                cancellableAuthenticationCallback,
                mainHandler);
    }

    @Override
    public void cancel() {
        if (cancellableAuthenticationCallback != null) {
            cancellableAuthenticationCallback.cancel();
            cancellableAuthenticationCallback = null;
        }
    }
}
