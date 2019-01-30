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
        cancel();
        this.internalCallback = new InternalCallback(
            this.cryptographyHandler,
            Mode.AUTHENTICATION,
            new CryptographyData("", ""),
            callback
        );
        biometricPrompt = new BiometricPrompt(params.getActivity(), executor, internalCallback);
        biometricPrompt.authenticate(params.getPromptInfo());
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

    private boolean isCryptographyDataInvalid(@Nullable CryptographyData cryptographyData) {
        return cryptographyData == null
            || cryptographyData.getKeyName().isEmpty()
            || cryptographyData.getValue().isEmpty();
    }

    private void startFingerprintAuthentication(
        @NonNull final Mode mode,
        @NonNull final GoldfingerParams params,
        @NonNull final GoldfingerCallback callback
    ) {
        cancel();
        log("Creating CryptoObject");
        if (isCryptographyDataInvalid(params.getCryptographyData())) {
            callback.onError(Error.INVALID_PARAMS);
            return;
        }
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
        this.biometricPrompt.authenticate(params.getPromptInfo(), cryptoObject);
    }
}
