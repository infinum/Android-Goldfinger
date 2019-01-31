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

@RequiresApi(Build.VERSION_CODES.M)
class MarshmallowGoldfinger implements Goldfinger {

    private final AsyncCryptoObjectFactory asyncCryptoFactory;
    private AsyncCryptoObjectFactory.Callback asyncCryptoFactoryCallback;
    private BiometricPrompt biometricPrompt;
    private final Context context;
    private final CryptographyHandler cryptographyHandler;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private InternalCallback internalCallback;

    MarshmallowGoldfinger(
        @NonNull Context context,
        @NonNull AsyncCryptoObjectFactory asyncCryptoFactory,
        @NonNull CryptographyHandler cryptographyHandler
    ) {
        this.context = context;
        this.asyncCryptoFactory = asyncCryptoFactory;
        this.cryptographyHandler = cryptographyHandler;
    }

    @Override
    public void authenticate(
        @NonNull GoldfingerParams params,
        @NonNull GoldfingerCallback callback
    ) {
        if (areParamsInvalid(params, Mode.AUTHENTICATION)) {
            callback.onError(Error.INVALID_PARAMS);
            return;
        }

        cancel();
        this.internalCallback = new InternalCallback(
            this.cryptographyHandler,
            Mode.AUTHENTICATION,
            new CryptographyData("", ""),
            callback
        );
        biometricPrompt = new BiometricPrompt(params.getActivity(), executor, internalCallback);
        biometricPrompt.authenticate(params.buildPromptInfo());
    }

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

    @Override
    public void decrypt(
        @NonNull GoldfingerParams params,
        @NonNull GoldfingerCallback callback
    ) {
        startFingerprintAuthentication(Mode.DECRYPTION, params, callback);
    }

    @Override
    public void encrypt(
        @NonNull GoldfingerParams params,
        @NonNull GoldfingerCallback callback
    ) {
        startFingerprintAuthentication(Mode.ENCRYPTION, params, callback);
    }

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

            if (cryptographyData.getKeyName().isEmpty()) {
                invalid = true;
                log("GoldfingerParams must contain non-empty = [CryptographyData#keyName]");
            }

            if (cryptographyData.getValue().isEmpty()) {
                invalid = true;
                log("GoldfingerParams must contain non-empty = [CryptographyData#value]");
            }
        }

        return invalid;
    }

    private void startFingerprintAuthentication(
        @NonNull final Mode mode,
        @NonNull final GoldfingerParams params,
        @NonNull final GoldfingerCallback callback
    ) {
        if (areParamsInvalid(params, mode)) {
            callback.onError(Error.INVALID_PARAMS);
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
                    callback.onError(Error.CRYPTO_OBJECT_CREATE_FAILED);
                }
            }
        };
        asyncCryptoFactory.createCryptoObject(params.getCryptographyData(), mode, asyncCryptoFactoryCallback);
    }

    private void startNativeFingerprintAuthentication(
        @NonNull Mode mode,
        @NonNull BiometricPrompt.CryptoObject cryptoObject,
        @NonNull GoldfingerParams params,
        @NonNull GoldfingerCallback callback
    ) {
        CryptographyData cryptographyData = params.getCryptographyData();
        log("Starting authentication [keyName=%s; value=%s]", cryptographyData.getKeyName(), cryptographyData.getValue());
        this.internalCallback = new InternalCallback(cryptographyHandler, mode, cryptographyData, callback);
        this.biometricPrompt = new BiometricPrompt(params.getActivity(), executor, internalCallback);
        this.biometricPrompt.authenticate(params.buildPromptInfo(), cryptoObject);
    }
}
