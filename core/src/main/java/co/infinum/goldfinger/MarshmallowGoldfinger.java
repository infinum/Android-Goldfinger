package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import static co.infinum.goldfinger.LogUtils.log;

@RequiresApi(Build.VERSION_CODES.M)
class MarshmallowGoldfinger implements Goldfinger {

    private static final String KEY_AUTH_MODE = "<Goldfinger authentication mode>";
    private final AsyncCryptoFactory asyncCryptoFactory;
    private AsyncCryptoFactory.Callback asyncCryptoFactoryCallback;
    private CancellableAuthenticationCallback cancellableAuthenticationCallback;
    private final Crypto crypto;
    private final FingerprintManagerCompat fingerprintManagerCompat;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    MarshmallowGoldfinger(Context context, AsyncCryptoFactory asyncCryptoFactory, Crypto crypto) {
        this.asyncCryptoFactory = asyncCryptoFactory;
        this.crypto = crypto;
        this.fingerprintManagerCompat = FingerprintManagerCompat.from(context);
    }

    @Override
    public void authenticate(Callback callback) {
        startFingerprintAuthentication(KEY_AUTH_MODE, "", Mode.AUTHENTICATION, callback);
    }

    @Override
    public void cancel() {
        if (cancellableAuthenticationCallback != null) {
            cancellableAuthenticationCallback.cancel();
            cancellableAuthenticationCallback = null;
        }

        if (asyncCryptoFactoryCallback != null) {
            asyncCryptoFactoryCallback.cancel();
            asyncCryptoFactoryCallback = null;
        }
    }

    @Override
    public void decrypt(String keyName, String value, Callback callback) {
        startFingerprintAuthentication(keyName, value, Mode.DECRYPTION, callback);
    }

    @Override
    public void encrypt(String keyName, String value, Callback callback) {
        startFingerprintAuthentication(keyName, value, Mode.ENCRYPTION, callback);
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        return fingerprintManagerCompat.hasEnrolledFingerprints();
    }

    @Override
    public boolean hasFingerprintHardware() {
        return fingerprintManagerCompat.isHardwareDetected();
    }

    private void notifyCryptoObjectInitError(Callback callback) {
        log("Failed to create CryptoObject");
        callback.onError(Error.INITIALIZATION_FAILED);
    }

    private void startFingerprintAuthentication(final String keyName, final String value, final Mode mode, final Callback callback) {
        cancel();

        log("Creating CryptoObject");
        asyncCryptoFactoryCallback = new AsyncCryptoFactory.Callback() {
            @Override
            void onCryptoObjectCreated(@Nullable FingerprintManagerCompat.CryptoObject cryptoObject) {
                if (cryptoObject != null) {
                    startNativeFingerprintAuthentication(cryptoObject, keyName, value, mode, callback);
                } else {
                    notifyCryptoObjectInitError(callback);
                }
            }
        };
        asyncCryptoFactory.createCryptoObject(keyName, mode, asyncCryptoFactoryCallback);
    }

    private void startNativeFingerprintAuthentication(@Nullable FingerprintManagerCompat.CryptoObject cryptoObject, String keyName,
        String value, Mode mode, Callback callback) {

        log("Starting authentication [keyName=%s; value=%s]", keyName, value);
        callback.onReady();
        cancellableAuthenticationCallback = new CancellableAuthenticationCallback(crypto, Clock.instance(), mode, value, callback);
        fingerprintManagerCompat.authenticate(
            cryptoObject,
            0,
            cancellableAuthenticationCallback.cancellationSignal,
            cancellableAuthenticationCallback,
            mainHandler
        );
    }
}
