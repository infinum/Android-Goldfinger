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
 * Goldfinger implementation for Android Marshmallow and newer.
 * Older versions use {@link LegacyGoldfinger}.
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

    /**
     * @see Goldfinger
     */
    @Override
    public void authenticate(@NonNull Callback callback) {
        startFingerprintAuthentication(KEY_AUTH_MODE, "", Mode.AUTHENTICATION, callback);
    }

    /**
     * @see Goldfinger
     */
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

    /**
     * @see Goldfinger
     */
    @Override
    public void decrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback) {
        startFingerprintAuthentication(keyName, value, Mode.DECRYPTION, callback);
    }

    /**
     * @see Goldfinger
     */
    @Override
    public void encrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback) {
        startFingerprintAuthentication(keyName, value, Mode.ENCRYPTION, callback);
    }

    /**
     * @see Goldfinger
     */
    @Override
    public boolean hasEnrolledFingerprint() {
        return fingerprintManagerCompat.hasEnrolledFingerprints();
    }

    /**
     * @see Goldfinger
     */
    @Override
    public boolean hasFingerprintHardware() {
        return fingerprintManagerCompat.isHardwareDetected();
    }

    /**
     * Notify {@link Goldfinger.Callback} that CryptoObject failed to create.
     */
    private void notifyCryptoObjectInitError(@NonNull Callback callback) {
        log("Failed to create CryptoObject");
        callback.onError(new InitializationException());
    }

    /**
     * Check preconditions:
     * 1) Device must have fingerprint hardware
     * 2) Device must have at least 1 enrolled fingerprint.
     * 3) Authentication is not active.
     *
     * @return true if preconditions are invalid, otherwise false.
     */
    private boolean preconditionsInvalid(Callback callback) {
        if (!hasFingerprintHardware()) {
            callback.onError(new MissingHardwareException());
            return true;
        }

        if (!hasEnrolledFingerprint()) {
            callback.onError(new NoEnrolledFingerprintsException());
            return true;
        }

        return cancellableAuthenticationCallback != null && cancellableAuthenticationCallback.isAuthenticationActive;
    }

    /**
     * Starts fingerprint authentication if preconditions are valid.
     *
     * @see MarshmallowGoldfinger#authenticate(Callback)
     * @see MarshmallowGoldfinger#encrypt(String, String, Callback)
     * @see MarshmallowGoldfinger#encrypt(String, String, Callback)
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
     * Start native authentication with successfully created CryptoObject.
     *
     * @see MarshmallowGoldfinger#startFingerprintAuthentication(String, String, Mode, Callback)
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
        cancellableAuthenticationCallback = new CancellableAuthenticationCallback(crypto, mode, value, callback);
        fingerprintManagerCompat.authenticate(
            cryptoObject,
            0,
            cancellableAuthenticationCallback.cancellationSignal,
            cancellableAuthenticationCallback,
            mainHandler
        );
    }
}
