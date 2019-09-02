package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Goldfinger implementation for Android Marshmallow and above
 * For implementation on Android older version
 * @see LegacyGoldfinger
 */
@RequiresApi(Build.VERSION_CODES.M)
class MarshmallowGoldfinger implements Goldfinger {

    private static final String KEY_AUTH_MODE = "<Goldfinger authentication mode>";

    @NonNull private final AsyncCryptoFactory asyncCryptoFactory;
    @NonNull private final Crypto crypto;
    @NonNull private final FingerprintManagerCompat fingerprintManagerCompat;
    @NonNull private final Handler mainHandler = new Handler(Looper.getMainLooper());
    @Nullable private AsyncCryptoFactory.Callback asyncCryptoFactoryCallback;
    @Nullable private CancellableAuthenticationCallback cancellableAuthenticationCallback;

    MarshmallowGoldfinger(@NonNull Context context, @NonNull AsyncCryptoFactory asyncCryptoFactory, @NonNull Crypto crypto) {
        this.asyncCryptoFactory = asyncCryptoFactory;
        this.crypto = crypto;
        this.fingerprintManagerCompat = FingerprintManagerCompat.from(context);
    }

    @Override
    public void authenticate(@NonNull Callback callback) {
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
    public void decrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback) {
        startFingerprintAuthentication(keyName, value, Mode.DECRYPTION, callback);
    }

    @Override
    public void encrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback) {
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

    /**
     * Notify Goldfinger.Callback that the creation of CryptoObject is failed thus
     * startNativeFingerAuthentication() method never been called.
     * @param callback  {@link Goldfinger.Callback} an object to receive authentication events
     */
    private void notifyCryptoObjectInitError(@NonNull Callback callback) {
        log("Failed to create CryptoObject");
        callback.onError(new InitializationException());
    }

    private boolean preconditionsInvalid(Callback callback) {
        if (!hasFingerprintHardware()) {
            callback.onError(new MissingHardwareException());
            return true;
        }

        if (!hasEnrolledFingerprint()) {
            callback.onError(new NoEnrolledFingerprintsException());
            return true;
        }
        return false;
    }

    /**
     * Create CryptoObject using AsyncCryptoFactory and use it for
     * startNativeFingerAuthentication() method
     * @param keyName   unique key identifier, {@link java.security.Key} is stored to
     *      *           {@link java.security.KeyStore} under this value
     * @param value     String value which will be encrypted if user successfully authenticates.
     *                  Value will be "" if Mode.AUTHENTICATION
     * @param mode      Mode to differentiate Fingerprint authentication modes.{@link Mode}
     * @param callback  {@link Goldfinger.Callback} an object to receive authentication events
     */
    private void startFingerprintAuthentication(
        @NonNull final String keyName,
        @NonNull final String value,
        @NonNull final Mode mode,
        @NonNull final Callback callback
    ) {
        if (preconditionsInvalid(callback)) {
            return;
        }

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

    /**
     * Request authentication of a crypto object.
     * This call warms up the fingerprint hardware and starts scanning for a fingerprint.
     * @param cryptoObject FingerprintManagerCompat.CryptoObject: object associated with the
     *                     call or null if none required.
     * @param keyName   unique key identifier, {@link java.security.Key} is stored to
     *                  {@link java.security.KeyStore} under this value
     * @param value     String value which will be encrypted if user successfully authenticates.
     *                  Value will be "" if Mode.AUTHENTICATION
     * @param mode      Mode to differentiate Fingerprint authentication modes.{@link Mode}
     * @param callback  {@link Goldfinger.Callback} an object to receive authentication events
     */
    private void startNativeFingerprintAuthentication(
        @Nullable FingerprintManagerCompat.CryptoObject cryptoObject,
        @NonNull String keyName,
        @NonNull String value,
        @NonNull Mode mode,
        @NonNull Callback callback
    ) {

        log("Starting authentication [keyName=%s; value=%s]", keyName, value);
        callback.onResult(new Goldfinger.Result(Type.INFO, Reason.AUTHENTICATION_START));
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
