package co.infinum.goldfinger;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;

import static co.infinum.goldfinger.LogUtils.log;

/**
 * Goldfinger implementation for Android Marshmallow and newer.
 * Older versions use {@link LegacyGoldfinger}.
 */
@RequiresApi(Build.VERSION_CODES.M)
class MarshmallowGoldfinger implements Goldfinger {

    @NonNull private final AsyncCryptoObjectFactory asyncCryptoFactory;
    @Nullable private AsyncCryptoObjectFactory.Callback asyncCryptoFactoryCallback;
    @Nullable private BiometricPrompt biometricPrompt;
    @NonNull private final Context context;
    @NonNull private final CryptographyHandler cryptographyHandler;
    @NonNull private final Executor executor = Executors.newSingleThreadExecutor();
    @Nullable private BiometricCallback biometricCallback;

    MarshmallowGoldfinger(
        @NonNull Context context,
        @NonNull AsyncCryptoObjectFactory asyncCryptoFactory,
        @NonNull CryptographyHandler cryptographyHandler
    ) {
        this.context = context;

        this.asyncCryptoFactory = asyncCryptoFactory;
        this.cryptographyHandler = cryptographyHandler;
    }

    /**
     * @see Goldfinger
     */
    @Override
    public void authenticate(
        @NonNull Params params,
        @NonNull Callback callback
    ) {
        if (internalCallback != null && internalCallback.isAuthenticationActive) {
            return;
        }

        if (areParamsInvalid(params, Mode.AUTHENTICATION)) {
            callback.onError(new InitializationException());
            return;
        }

        cancel();
        this.internalCallback = new AuthenticationCallback(
            this.cryptographyHandler,
            Mode.AUTHENTICATION,
            new CryptographyData("", ""),
            callback
        );
        biometricPrompt = new BiometricPrompt(params.getActivity(), executor, internalCallback);
        biometricPrompt.authenticate(params.buildPromptInfo());
    }

    /**
     * @see Goldfinger
     */
    @Override
    public void cancel() {
        if (biometricPrompt != null) {
            biometricPrompt.cancelAuthentication();
            biometricPrompt = null;
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
    public void decrypt(
        @NonNull GoldfingerParams params,
        @NonNull Goldfinger.Callback callback
    ) {
        startFingerprintAuthentication(Mode.DECRYPTION, params, callback);
    }

    /**
     * @see Goldfinger
     */
    @Override
    public void encrypt(
        @NonNull GoldfingerParams params,
        @NonNull Goldfinger.Callback callback
    ) {
        startFingerprintAuthentication(Mode.ENCRYPTION, params, callback);
    }

    /**
     * @see Goldfinger
     */
    @Override
    public boolean hasFingerprintHardware() {
        return context.getPackageManager() != null
            && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    }

    private boolean areParamsInvalid(@NonNull GoldfingerParams params, @NonNull Mode mode) {
        boolean invalid = false;

        if (params.getTitle().isEmpty()) {
            invalid = true;
            log("GoldfingerParams must contain non-empty = [title]");
        }

        if (params.getNegativeButtonText().isEmpty()) {
            invalid = true;
            log("GoldfingerParams must contain non-empty = [negativeButtonText]");
        }

        if (Mode.AUTHENTICATION != mode) {
            CryptographyData cryptographyData = params.getCryptographyData();

            if (cryptographyData.keyName().isEmpty()) {
                invalid = true;
                log("GoldfingerParams must contain non-empty = [CryptographyData#keyName]");
            }

            if (cryptographyData.value().isEmpty()) {
                invalid = true;
                log("GoldfingerParams must contain non-empty = [CryptographyData#value]");
            }
        }

        return invalid;
    }

    private void startFingerprintAuthentication(
        @NonNull final Mode mode,
        @NonNull final GoldfingerParams params,
        @NonNull final Goldfinger.Callback callback
    ) {
        if (internalCallback != null && internalCallback.isAuthenticationActive) {
            return;
        }

        if (areParamsInvalid(params, mode)) {
            callback.onError(new InitializationException());
            return;
        }

        cancel();
        log("Creating CryptoObject");
        asyncCryptoFactoryCallback = new AsyncCryptoObjectFactory.Callback() {
            @Override
            void onCryptoObjectCreated(@Nullable BiometricPrompt.CryptoObject cryptoObject) {
                if (cryptoObject != null) {
                    startNativeFingerprintAuthentication(mode, cryptoObject, params, callback);
                } else {
                    log("Failed to create CryptoObject");
                    callback.onError(new InitializationException());
                }
            }
        };
        asyncCryptoFactory.createCryptoObject(params.getCryptographyData(), mode, asyncCryptoFactoryCallback);
    }

    private void startNativeFingerprintAuthentication(
        @NonNull Mode mode,
        @NonNull BiometricPrompt.CryptoObject cryptoObject,
        @NonNull GoldfingerParams params,
        @NonNull Goldfinger.Callback callback
    ) {
        CryptographyData cryptographyData = params.getCryptographyData();
        log("Starting authentication [keyName=%s; value=%s]", cryptographyData.keyName(), cryptographyData.value());
        callback.onResult(new Result(Type.INFO, Reason.AUTHENTICATION_START));
        this.internalCallback = new AuthenticationCallback(cryptographyHandler, mode, cryptographyData, callback);
        this.biometricPrompt = new BiometricPrompt(params.getActivity(), executor, internalCallback);
        this.biometricPrompt.authenticate(params.buildPromptInfo(), cryptoObject);
    }
}
