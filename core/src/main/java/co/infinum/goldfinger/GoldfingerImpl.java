package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

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
 * Older versions use {@link GoldfingerMock}.
 */
@RequiresApi(Build.VERSION_CODES.M)
class GoldfingerImpl implements Goldfinger {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    @NonNull private final AsyncCryptoObjectFactory asyncCryptoFactory;
    @Nullable private AsyncCryptoObjectFactory.Callback asyncCryptoFactoryCallback;
    @Nullable private BiometricPrompt biometricPrompt;
    @NonNull private final CrypterProxy cryptoProxy;
    @NonNull private final BiometricManager biometricManager;
    @NonNull private final Executor executor = Executors.newSingleThreadExecutor();
    @Nullable private BiometricCallback biometricCallback;
    private boolean creatingCryptoObject = false;

    GoldfingerImpl(
        @NonNull Context context,
        @NonNull AsyncCryptoObjectFactory asyncCryptoFactory,
        @NonNull CrypterProxy cryptoProxy
    ) {
        this.biometricManager = BiometricManager.from(context);
        this.asyncCryptoFactory = asyncCryptoFactory;
        this.cryptoProxy = cryptoProxy;
    }

    /**
     * @see Goldfinger#authenticate
     */
    @Override
    public void authenticate(
        @NonNull PromptParams params,
        @NonNull Callback callback
    ) {
        if (preconditionsInvalid(params, Mode.AUTHENTICATION, null, null, callback)) {
            return;
        }

        log("Starting authentication");
        startNativeBiometricAuthentication(params, Mode.AUTHENTICATION, null, null, callback, null);
    }

    @Override
    public boolean canAuthenticate() {
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
    }

    @Override
    public boolean canAuthenticate(int authenticators) {
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS;
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
    public void decrypt(@NonNull PromptParams params, @NonNull String key, @NonNull String value, @NonNull Callback callback) {
        if (preconditionsInvalid(params, Mode.DECRYPTION, key, value, callback)) {
            return;
        }

        initializeCryptoObject(params, Mode.DECRYPTION, key, value, callback);
    }

    @Override
    public void encrypt(@NonNull PromptParams params, @NonNull String key, @NonNull String value, @NonNull Callback callback) {
        if (preconditionsInvalid(params, Mode.ENCRYPTION, key, value, callback)) {
            return;
        }

        initializeCryptoObject(params, Mode.ENCRYPTION, key, value, callback);
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        int authenticationStatus = biometricManager.canAuthenticate();
        return authenticationStatus != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            && authenticationStatus != BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
    }

    @Override
    public boolean hasEnrolledFingerprint(int authenticators) {
        int authenticationStatus = biometricManager.canAuthenticate(authenticators);
        return authenticationStatus != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            && authenticationStatus != BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
    }

    @Override
    public boolean hasEnrolledBiometrics(int authenticators) {
        int authenticationStatus = biometricManager.canAuthenticate(authenticators);
        return authenticationStatus != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            && authenticationStatus != BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
    }

    @Override
    public boolean hasFingerprintHardware() {
        int authenticationStatus = biometricManager.canAuthenticate();
        return authenticationStatus != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
            && authenticationStatus != BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
    }

    @Override
    public boolean hasFingerprintHardware(int authenticators) {
        int authenticationStatus = biometricManager.canAuthenticate(authenticators);
        return authenticationStatus != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
            && authenticationStatus != BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
    }

    @Override
    public boolean hasBiometricHardware(int authenticators) {
        int authenticationStatus = biometricManager.canAuthenticate(authenticators);
        return authenticationStatus != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
            && authenticationStatus != BiometricManager.BIOMETRIC_STATUS_UNKNOWN;
    }

    private void initializeCryptoObject(
        @NonNull final PromptParams params,
        @NonNull final Mode mode,
        @NonNull final String key,
        @NonNull final String value,
        @NonNull final Callback callback
    ) {
        log("Creating CryptoObject");
        asyncCryptoFactoryCallback = new AsyncCryptoObjectFactory.Callback() {
            @Override
            void onCryptoObjectCreated(@Nullable BiometricPrompt.CryptoObject cryptoObject) {
                creatingCryptoObject = false;
                if (cryptoObject != null) {
                    startNativeBiometricAuthentication(params, mode, key, value, callback, cryptoObject);
                } else {
                    log("Failed to create CryptoObject");
                    callback.onError(new CryptoObjectInitException());
                }
            }
        };
        creatingCryptoObject = true;
        asyncCryptoFactory.createCryptoObject(mode, key, asyncCryptoFactoryCallback);
    }

    private boolean preconditionsInvalid(PromptParams params, Mode mode, String key, String value, Callback callback) {
        if ((biometricCallback != null && biometricCallback.isAuthenticationActive) || creatingCryptoObject) {
            log("Authentication is already active. Ignoring authenticate call.");
            return true;
        }

        if (!hasFingerprintHardware(params.allowedAuthenticators())) {
            callback.onError(new MissingHardwareException());
            return true;
        }

        if (!hasEnrolledFingerprint(params.allowedAuthenticators())) {
            callback.onError(new NoEnrolledBiometricsException());
            return true;
        }

        List<String> promptParams = ValidateUtils.validatePromptParams(mode, params);
        if (!promptParams.isEmpty()) {
            callback.onError(new InvalidParametersException(promptParams));
            return true;
        }

        List<String> cipherErrors = ValidateUtils.validateCipherParams(mode, key, value);
        if (!cipherErrors.isEmpty()) {
            callback.onError(new InvalidParametersException(cipherErrors));
            return true;
        }

        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private void startNativeBiometricAuthentication(
        @NonNull final PromptParams params,
        @NonNull final Mode mode,
        @Nullable final String key,
        @Nullable final String value,
        @NonNull final Callback callback,
        @Nullable final BiometricPrompt.CryptoObject cryptoObject
    ) {
        /*
         * Use proxy callback because some devices do not cancel authentication when error is received.
         * Cancel authentication manually and proxy the result to real callback.
         */
        this.biometricCallback = new BiometricCallback(cryptoProxy, mode, value, new Callback() {
            @Override
            public void onError(@NonNull Exception e) {
                cancel();
                callback.onError(e);
            }

            @Override
            public void onResult(@NonNull Result result) {
                if (result.type() == Type.ERROR || result.type() == Type.SUCCESS) {
                    cancel();
                }
                callback.onResult(result);
            }
        });

        if (params.dialogOwner() instanceof FragmentActivity) {
            this.biometricPrompt = new BiometricPrompt((FragmentActivity) params.dialogOwner(), executor, biometricCallback);
        }
        if (params.dialogOwner() instanceof Fragment) {
            this.biometricPrompt = new BiometricPrompt((Fragment) params.dialogOwner(), executor, biometricCallback);
        }

        /* Delay with post because Navigation and Prompt both work with Fragment transactions */
        MAIN_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (GoldfingerImpl.this.biometricPrompt == null) {
                    return;
                }

                if (mode == Mode.AUTHENTICATION) {
                    /* Simple Authentication call */
                    log("Starting authentication");
                    callback.onResult(new Result(Type.INFO, Reason.AUTHENTICATION_START));
                    GoldfingerImpl.this.biometricPrompt.authenticate(params.buildPromptInfo());
                } else {
                    /* Encryption/Decryption call with initialized CryptoObject */
                    log("Starting authentication [keyName=%s; value=%s]", key, value);
                    callback.onResult(new Result(Type.INFO, Reason.AUTHENTICATION_START));
                    GoldfingerImpl.this.biometricPrompt.authenticate(params.buildPromptInfo(), cryptoObject);
                }
            }
        });
    }
}
