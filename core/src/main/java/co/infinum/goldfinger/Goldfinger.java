package co.infinum.goldfinger;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public interface Goldfinger {

    /**
     * Returns true if device has fingerprint hardware, false otherwise.
     */
    boolean hasFingerprintHardware();

    /**
     * Authenticate user via Fingerprint.
     * <p>
     * Example - Process payment after successful fingerprint authentication.
     *
     * @see GoldfingerParams
     */
    void authenticate(@NonNull GoldfingerParams params, @NonNull Callback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link CryptographyHandler} implementation is used to automatically decrypt given value.
     *
     * @see GoldfingerParams
     */
    void decrypt(@NonNull GoldfingerParams params, @NonNull Callback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link CryptographyHandler} implementation is used to automatically encrypt given value.
     * <p>
     * Use it when saving some data that should not be saved as plain text (e.g. password).
     * To decrypt the value use {@link Goldfinger#decrypt(GoldfingerParams, GoldfingerCallback)}} method.
     *
     * @see GoldfingerParams
     * @see GoldfingerCallback
     */
    void encrypt(@NonNull GoldfingerParams params, @NonNull Callback callback);

    /**
     * Cancel current active Fingerprint authentication.
     */
    void cancel();

    /**
     * Become Bob the builder.
     */
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    class Builder {

        @NonNull private final Context context;
        @Nullable private CryptoObjectFactory cryptoObjectFactory;
        @Nullable private CryptographyHandler cryptographyHandler;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        public Goldfinger build() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return buildMarshmallowInstance();
            } else {
                return new LegacyGoldfinger();
            }
        }

        @NonNull
        public Builder cryptoObjectFactory(@Nullable CryptoObjectFactory cryptoObjectFactory) {
            this.cryptoObjectFactory = cryptoObjectFactory;
            return this;
        }

        @NonNull
        public Builder cryptographyHandler(@Nullable CryptographyHandler cryptographyHandler) {
            this.cryptographyHandler = cryptographyHandler;
            return this;
        }

        @NonNull
        public Builder logEnabled(boolean logEnabled) {
            LogUtils.setEnabled(logEnabled);
            return this;
        }

        @NonNull
        @RequiresApi(Build.VERSION_CODES.M)
        private Goldfinger buildMarshmallowInstance() {
            CryptographyHandler handler = cryptographyHandler != null ? cryptographyHandler : new CryptographyHandler.Default();
            CryptoObjectFactory factory = cryptoObjectFactory != null ? cryptoObjectFactory : new CryptoObjectFactory.Default(context);
            AsyncCryptoObjectFactory asyncFactory = new AsyncCryptoObjectFactory(factory);
            return new MarshmallowGoldfinger(context, asyncFactory, handler);
        }
    }

    /**
     * Result wrapper class containing all the useful information about
     * fingerprint authentication and value for encryption/decryption operations.
     */
    class Result {

        /**
         * @see Goldfinger.Type
         */
        @NonNull private final Goldfinger.Type type;

        /**
         * @see Goldfinger.Reason
         */
        @NonNull private final Goldfinger.Reason reason;

        /**
         * Authentication value. If {@link Goldfinger#authenticate(Callback)} method is used,
         * returned value is null, otherwise returned value contains encrypted or decrypted
         * String, IFF type is {@link Type#SUCCESS}
         * <p>
         * In all other cases, the value is null.
         */
        @Nullable private final String value;

        /**
         * System message returned by {@link androidx.core.hardware.fingerprint.FingerprintManagerCompat.AuthenticationCallback}.
         * A human-readable error string that can be shown in UI.
         */
        @Nullable private final String message;

        Result(@NonNull Type type, @NonNull Reason reason) {
            this(type, reason, null, null);
        }

        Result(@NonNull Type type, @NonNull Reason reason, @Nullable String value, @Nullable String message) {
            this.type = type;
            this.reason = reason;
            this.value = value;
            this.message = message;
        }

        @Nullable
        public String message() {
            return message;
        }

        @NonNull
        public Reason reason() {
            return reason;
        }

        @NonNull
        public Type type() {
            return type;
        }

        @Nullable
        public String value() {
            return value;
        }
    }

    interface Callback {

        /**
         * Returns fingerprint result and will be invoked multiple times during
         * fingerprint authentication as not all fingerprint results complete
         * the authentication.
         *
         * @see Goldfinger.Result
         */
        void onResult(@NonNull Goldfinger.Result result);

        /**
         * Critical error happened and user fingerprint should be invalidated.
         */
        void onError(@NonNull Exception e);
    }

    enum Reason {
        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_HW_UNAVAILABLE
         */
        HARDWARE_UNAVAILABLE,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_UNABLE_TO_PROCESS
         */
        UNABLE_TO_PROCESS,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_TIMEOUT
         */
        TIMEOUT,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_NO_SPACE
         */
        NO_SPACE,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_CANCELED
         */
        CANCELED,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_LOCKOUT
         */
        LOCKOUT,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_VENDOR
         */
        VENDOR,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_LOCKOUT_PERMANENT
         */
        LOCKOUT_PERMANENT,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_USER_CANCELED
         */
        USER_CANCELED,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_NO_BIOMETRICS
         */
        NO_BIOMETRICS,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_HW_NOT_PRESENT
         */
        HW_NOT_PRESENT,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_NEGATIVE_BUTTON
         */
        NEGATIVE_BUTTON,

        /**
         * @see androidx.biometric.BiometricPrompt#ERROR_NO_DEVICE_CREDENTIAL
         */
        NO_DEVICE_CREDENTIAL,

        /**
         * Dispatched when Fingerprint authentication is initialized correctly and
         * just before actual authentication is started. Can be used to update UI if necessary.
         */
        AUTHENTICATION_START,

        /**
         * @see FingerprintManager.AuthenticationCallback#onAuthenticationSucceeded(FingerprintManager.AuthenticationResult)
         */
        AUTHENTICATION_SUCCESS,

        /**
         * @see FingerprintManager.AuthenticationCallback#onAuthenticationFailed()
         */
        AUTHENTICATION_FAIL,

        /**
         * Unknown reason.
         */
        UNKNOWN
    }

    enum Type {

        /**
         * Fingerprint authentication is successfully finished. {@link Goldfinger.Result}
         * will contain value in case of {@link Goldfinger#decrypt(String, String, Callback)} or
         * {@link Goldfinger#encrypt(String, String, Callback)} invocation.
         */
        SUCCESS,

        /**
         * Fingerprint authentication is still active. {@link Goldfinger.Result} contains
         * additional information about currently active fingerprint authentication.
         */
        INFO,

        /**
         * Fingerprint authentication is unsuccessfully finished. {@link Goldfinger.Result}
         * contains the reason why the authentication failed.
         */
        ERROR
    }
}
