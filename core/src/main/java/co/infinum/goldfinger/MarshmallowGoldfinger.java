package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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
    @NonNull private final CryptographyHandler cryptographyHandler;
    @NonNull private final BiometricManager biometricManager;
    @NonNull private final Executor executor = Executors.newSingleThreadExecutor();
    @Nullable private BiometricCallback biometricCallback;
    private boolean creatingCryptoObject = false;

    MarshmallowGoldfinger(
        @NonNull Context context,
        @NonNull AsyncCryptoObjectFactory asyncCryptoFactory,
        @NonNull CryptographyHandler cryptographyHandler
    ) {
        this.biometricManager = BiometricManager.from(context);
        this.asyncCryptoFactory = asyncCryptoFactory;
        this.cryptographyHandler = cryptographyHandler;
    }

    /**
     * @see Goldfinger#authenticate
     */
    @Override
    public void authenticate(
        @NonNull Params params,
        @NonNull Callback callback
    ) {
        if (preconditionsInvalid(params, callback)) {
            return;
        }

        if (params.mode() == Mode.AUTHENTICATION) {
            log("Starting authentication");
            startNativeFingerprintAuthentication(params, callback, null);
        } else {
            initializeCryptoObject(params, callback);
        }
    }

    @Override
    public boolean canAuthenticate() {
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
    }

    /**
     * @see Goldfinger#cancel
     */
    @Override
    public void cancel() {
        if (biometricPrompt != null) {
            biometricPrompt.cancelAuthentication();
            biometricPrompt = null;
        }

        if (biometricCallback != null) {
            biometricCallback.cancel();
            biometricCallback = null;
        }

        if (asyncCryptoFactoryCallback != null) {
            asyncCryptoFactoryCallback.cancel();
            asyncCryptoFactoryCallback = null;
        }
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        return biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
    }

    @Override
    public boolean hasFingerprintHardware() {
        return biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeCryptoObject(
        @NonNull final Params params,
        @NonNull final Callback callback
    ) {
        log("Creating CryptoObject");
        asyncCryptoFactoryCallback = new AsyncCryptoObjectFactory.Callback() {
            @Override
            void onCryptoObjectCreated(@Nullable BiometricPrompt.CryptoObject cryptoObject) {
                creatingCryptoObject = false;
                if (cryptoObject != null) {
                    startNativeFingerprintAuthentication(params, callback, cryptoObject);
                } else {
                    log("Failed to create CryptoObject");
                    callback.onError(new CryptoObjectInitException());
                }
            }
        };
        creatingCryptoObject = true;
        asyncCryptoFactory.createCryptoObject(params.mode(), params.key(), asyncCryptoFactoryCallback);
    }

    private boolean preconditionsInvalid(Params params, Callback callback) {
        if ((biometricCallback != null && biometricCallback.isAuthenticationActive) || creatingCryptoObject) {
            log("Authentication is already active. Ignoring authenticate call.");
            return true;
        }

        if (!hasFingerprintHardware()) {
            callback.onError(new MissingHardwareException());
            return true;
        }

        if (!hasEnrolledFingerprint()) {
            callback.onError(new NoEnrolledFingerprintException());
            return true;
        }

        List<String> errors = ValidateUtils.validateParams(params);
        if (!errors.isEmpty()) {
            callback.onError(new InvalidParametersException(errors));
            return true;
        }

        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private void startNativeFingerprintAuthentication(
        @NonNull Params params,
        @NonNull Callback callback,
        @Nullable BiometricPrompt.CryptoObject cryptoObject
    ) {
        callback.onResult(new Result(Type.INFO, Reason.AUTHENTICATION_START));
        this.biometricCallback = new BiometricCallback(cryptographyHandler, params.mode(), params.value(), callback);
        if (params.dialogOwner() instanceof FragmentActivity) {
            this.biometricPrompt = new BiometricPrompt((FragmentActivity) params.dialogOwner(), executor, biometricCallback);
        }
        if (params.dialogOwner() instanceof Fragment) {
            this.biometricPrompt = new BiometricPrompt((Fragment) params.dialogOwner(), executor, biometricCallback);
        }

        if (cryptoObject != null) {
            /* Encryption/Decryption call with initialized CryptoObject */
            log("Starting authentication [keyName=%s; value=%s]", params.key(), params.value());
            this.biometricPrompt.authenticate(params.buildPromptInfo(), cryptoObject);
        } else {
            /* Simple Authentication call */
            log("Starting authentication");
            this.biometricPrompt.authenticate(params.buildPromptInfo());
        }
    }
}
