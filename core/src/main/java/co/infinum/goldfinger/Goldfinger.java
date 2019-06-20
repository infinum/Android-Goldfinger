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
     * Returns true if user has fingerprint in device settings, false otherwise.
     */
    boolean hasEnrolledFingerprint();

    /**
     * Authenticate user via Fingerprint.
     * <p>
     * Example - Process payment after successful fingerprint authentication.
     */
    void authenticate(@NonNull Callback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link Crypto} implementation is used to automatically decrypt given value.
     * <p>
     * Should be used together with {@link Goldfinger#encrypt(String, String, Callback)} to decrypt saved data.
     *
     * @param keyName unique key identifier, {@link java.security.Key} saved under this value is loaded from {@link java.security.KeyStore}
     * @param value   String value which will be decrypted if user successfully authenticates
     */
    void decrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link Crypto} implementation is used to automatically encrypt given value.
     * <p>
     * Use it when saving some data that should not be saved as plain text (e.g. password).
     * To decrypt the value use {@link Goldfinger#decrypt(String, String, Callback)} method.
     * <p>
     * Example - Allow auto-login via Fingerprint.
     *
     * @param keyName unique key identifier, {@link java.security.Key} is stored to {@link java.security.KeyStore} under this value
     * @param value   String value which will be encrypted if user successfully authenticates
     * @see Goldfinger.Callback
     */
    void encrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback);

    /**
     * Cancel current active Fingerprint authentication.
     */
    void cancel();

    /**
     * Become Bob the builder.
     */
    class Builder {

        @NonNull private final Context context;
        @Nullable private Crypto crypto;
        @Nullable private CryptoFactory cryptoFactory;

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
        public Builder setCrypto(@Nullable Crypto crypto) {
            this.crypto = crypto;
            return this;
        }

        @NonNull
        public Builder setCryptoFactory(@Nullable CryptoFactory cryptoFactory) {
            this.cryptoFactory = cryptoFactory;
            return this;
        }

        @NonNull
        public Builder setLogEnabled(boolean logEnabled) {
            LogUtils.setEnabled(logEnabled);
            return this;
        }

        @NonNull
        @RequiresApi(Build.VERSION_CODES.M)
        private Goldfinger buildMarshmallowInstance() {
            Crypto finalCrypto = crypto != null ? crypto : new Crypto.Default();
            CryptoFactory finalCryptoFactory =
                cryptoFactory != null ? cryptoFactory : new CryptoFactory.Default(context);
            AsyncCryptoFactory asyncCryptoFactory = new AsyncCryptoFactory(finalCryptoFactory);
            return new MarshmallowGoldfinger(context, asyncCryptoFactory, finalCrypto);
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
         * @see FingerprintManager#FINGERPRINT_ERROR_HW_UNAVAILABLE
         */
        HARDWARE_UNAVAILABLE,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_UNABLE_TO_PROCESS
         */
        UNABLE_TO_PROCESS,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_TIMEOUT
         */
        TIMEOUT,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_NO_SPACE
         */
        NO_SPACE,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_CANCELED
         */
        CANCELED,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_LOCKOUT
         */
        LOCKOUT,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_VENDOR
         */
        VENDOR,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_LOCKOUT_PERMANENT
         */
        LOCKOUT_PERMANENT,

        /**
         * @see FingerprintManager#FINGERPRINT_ERROR_USER_CANCELED
         */
        USER_CANCELED,

        /**
         * @see FingerprintManager#FINGERPRINT_ACQUIRED_GOOD
         */
        GOOD,

        /**
         * @see FingerprintManager#FINGERPRINT_ACQUIRED_PARTIAL
         */
        PARTIAL,

        /**
         * @see FingerprintManager#FINGERPRINT_ACQUIRED_INSUFFICIENT
         */
        INSUFFICIENT,

        /**
         * @see FingerprintManager#FINGERPRINT_ACQUIRED_IMAGER_DIRTY
         */
        IMAGER_DIRTY,

        /**
         * @see FingerprintManager#FINGERPRINT_ACQUIRED_TOO_SLOW
         */
        TOO_SLOW,

        /**
         * @see FingerprintManager#FINGERPRINT_ACQUIRED_TOO_FAST
         */
        TOO_FAST,

        /**
         * Dispatched when Fingerprint authentication is initialized correctly and
         * just before actual authentication is started. Can be used to update UI if necessary.
         * <p>
         * Example - You want to display Dialog only if initialization is successful.
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
