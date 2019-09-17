package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

@SuppressWarnings("unused")
public interface Goldfinger {

    /**
     * Returns true if user has fingerprint hardware, false otherwise.
     */
    boolean hasFingerprintHardware();

    /**
     * Returns true if user has enrolled fingerprint, false otherwise.
     */
    boolean hasEnrolledFingerprint();

    /**
     * @see BiometricManager#canAuthenticate()
     */
    boolean canAuthenticate();

    /**
     * Authenticate user via Fingerprint.
     * <p>
     * Example - Process payment after successful fingerprint authentication.
     *
     * @see Params
     */
    void authenticate(@NonNull Params params, @NonNull Callback callback);

    /**
     * Cancel current active Fingerprint authentication.
     */
    void cancel();

    /**
     * Become Bob the builder.
     */
    @SuppressWarnings("UnusedReturnValue")
    class Builder {

        @NonNull private final Context context;
        @Nullable private CryptoObjectFactory cryptoObjectFactory;
        @Nullable private CryptographyHandler cryptographyHandler;
        @NonNull private Mode mode = Mode.AUTHENTICATION;
        @Nullable private String key;
        @Nullable private String value;

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

    @SuppressWarnings("WeakerAccess")
    class Params {

        @NonNull private final Object dialogOwner;
        @Nullable private final String description;
        @Nullable private final String negativeButtonText;
        @Nullable private final String subtitle;
        @Nullable private final String title;
        private final boolean confirmationRequired;
        private final boolean deviceCredentialsAllowed;
        @NonNull private final Mode mode;
        @Nullable private final String key;
        @Nullable private final String value;

        private Params(
            @NonNull Object dialogOwner,
            @Nullable String title,
            @Nullable String description,
            @Nullable String negativeButtonText,
            @Nullable String subtitle,
            boolean confirmationRequired,
            boolean deviceCredentialsAllowed,
            @NonNull Mode mode,
            @Nullable String key,
            @Nullable String value
        ) {
            this.dialogOwner = dialogOwner;
            this.title = title;
            this.description = description;
            this.negativeButtonText = negativeButtonText;
            this.subtitle = subtitle;
            this.confirmationRequired = confirmationRequired;
            this.deviceCredentialsAllowed = deviceCredentialsAllowed;
            this.mode = mode;
            this.key = key;
            this.value = value;
        }

        public boolean confirmationRequired() {
            return confirmationRequired;
        }

        @Nullable
        public String description() {
            return description;
        }

        public boolean deviceCredentialsAllowed() {
            return deviceCredentialsAllowed;
        }

        @NonNull
        public Object dialogOwner() {
            return dialogOwner;
        }

        @Nullable
        public String key() {
            return key;
        }

        @NonNull
        public Mode mode() {
            return mode;
        }

        @Nullable
        public String negativeButtonText() {
            return negativeButtonText;
        }

        @Nullable
        public String subtitle() {
            return subtitle;
        }

        @Nullable
        public String title() {
            return title;
        }

        /**
         * Create new {@link BiometricPrompt.PromptInfo} instance. Parameter
         * validation is done earlier in the code so we can trust the data at
         * this step.
         */
        @SuppressWarnings("ConstantConditions")
        @NonNull
        BiometricPrompt.PromptInfo buildPromptInfo() {
            BiometricPrompt.PromptInfo.Builder builder = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setDeviceCredentialAllowed(deviceCredentialsAllowed)
                .setConfirmationRequired(confirmationRequired);

            if (!deviceCredentialsAllowed) {
                builder.setNegativeButtonText(negativeButtonText);
            }
            return builder.build();
        }

        @Nullable
        String value() {
            return value;
        }

        public static class Builder {

            /* Dialog dialogOwner can be either Fragment or FragmentActivity */
            @NonNull private Object dialogOwner;
            @NonNull private Mode mode = Mode.AUTHENTICATION;
            @Nullable private String description;
            @Nullable private String negativeButtonText;
            @Nullable private String subtitle;
            @Nullable private String title;
            private boolean confirmationRequired;
            private boolean deviceCredentialsAllowed;
            @Nullable private String key;
            @Nullable private String value;

            public Builder(@NonNull FragmentActivity activity) {
                this.dialogOwner = activity;
            }

            public Builder(@NonNull Fragment fragment) {
                this.dialogOwner = fragment;
            }

            @NonNull
            public Params build() {
                return new Params(
                    dialogOwner,
                    title,
                    description,
                    negativeButtonText,
                    subtitle,
                    confirmationRequired,
                    deviceCredentialsAllowed,
                    mode,
                    key,
                    value
                );
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setConfirmationRequired
             */
            @NonNull
            public Builder confirmationRequired(boolean confirmationRequired) {
                this.confirmationRequired = confirmationRequired;
                return this;
            }

            @NonNull
            public Builder decrypt(@NonNull String key, @NonNull String value) {
                this.mode = Mode.DECRYPTION;
                this.key = key;
                this.value = value;
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setDescription
             */
            @NonNull
            public Builder description(@Nullable String description) {
                this.description = description;
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setDescription
             */
            @NonNull
            public Builder description(@StringRes int resId) {
                this.description = getString(resId);
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setDeviceCredentialAllowed
             */
            @NonNull
            public Builder deviceCredentialsAllowed(boolean deviceCredentialsAllowed) {
                this.deviceCredentialsAllowed = deviceCredentialsAllowed;
                return this;
            }

            @NonNull
            public Builder encrypt(@NonNull String key, @NonNull String value) {
                this.mode = Mode.ENCRYPTION;
                this.key = key;
                this.value = value;
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setNegativeButtonText
             */
            @NonNull
            public Builder negativeButtonText(@NonNull String negativeButtonText) {
                this.negativeButtonText = negativeButtonText;
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setNegativeButtonText
             */
            @NonNull
            public Builder negativeButtonText(@StringRes int resId) {
                this.negativeButtonText = getString(resId);
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setSubtitle
             */
            @NonNull
            public Builder subtitle(@Nullable String subtitle) {
                this.subtitle = subtitle;
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setSubtitle
             */
            @NonNull
            public Builder subtitle(@StringRes int resId) {
                this.subtitle = getString(resId);
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setTitle
             */
            @NonNull
            public Builder title(@NonNull String title) {
                this.title = title;
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setTitle
             */
            @NonNull
            public Builder title(@StringRes int resId) {
                this.title = getString(resId);
                return this;
            }

            @Nullable
            private String getString(@StringRes int resId) {
                if (dialogOwner instanceof Fragment) {
                    return ((Fragment) dialogOwner).getString(resId);
                }

                if (dialogOwner instanceof FragmentActivity) {
                    return ((FragmentActivity) dialogOwner).getString(resId);
                }

                return null;
            }
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
         * Authentication value. If standard {@link Goldfinger#authenticate} method is used,
         * returned value is null.
         * <p>
         * IFF {@link Params.Builder#encrypt} or {@link Params.Builder#decrypt}
         * is used, the value contains encrypted or decrypted String.
         * <p>
         * In all other cases, the value is null.
         */
        @Nullable private final String value;

        /**
         * System message returned by {@link BiometricPrompt.AuthenticationCallback}.
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
         * @see BiometricPrompt#ERROR_HW_UNAVAILABLE
         */
        HARDWARE_UNAVAILABLE,

        /**
         * @see BiometricPrompt#ERROR_UNABLE_TO_PROCESS
         */
        UNABLE_TO_PROCESS,

        /**
         * @see BiometricPrompt#ERROR_TIMEOUT
         */
        TIMEOUT,

        /**
         * @see BiometricPrompt#ERROR_NO_SPACE
         */
        NO_SPACE,

        /**
         * @see BiometricPrompt#ERROR_CANCELED
         */
        CANCELED,

        /**
         * @see BiometricPrompt#ERROR_LOCKOUT
         */
        LOCKOUT,

        /**
         * @see BiometricPrompt#ERROR_VENDOR
         */
        VENDOR,

        /**
         * @see BiometricPrompt#ERROR_LOCKOUT_PERMANENT
         */
        LOCKOUT_PERMANENT,

        /**
         * @see BiometricPrompt#ERROR_USER_CANCELED
         */
        USER_CANCELED,

        /**
         * @see BiometricPrompt#ERROR_NO_BIOMETRICS
         */
        NO_BIOMETRICS,

        /**
         * @see BiometricPrompt#ERROR_HW_NOT_PRESENT
         */
        HW_NOT_PRESENT,

        /**
         * @see BiometricPrompt#ERROR_NEGATIVE_BUTTON
         */
        NEGATIVE_BUTTON,

        /**
         * @see BiometricPrompt#ERROR_NO_DEVICE_CREDENTIAL
         */
        NO_DEVICE_CREDENTIAL,

        /**
         * Dispatched when Fingerprint authentication is initialized correctly and
         * just before actual authentication is started. Can be used to update UI if necessary.
         */
        AUTHENTICATION_START,

        /**
         * @see BiometricPrompt.AuthenticationCallback#onAuthenticationSucceeded
         */
        AUTHENTICATION_SUCCESS,

        /**
         * @see BiometricPrompt.AuthenticationCallback#onAuthenticationFailed
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
         * will contain value in case of {@link Params.Builder#decrypt} or
         * {@link Params.Builder#encrypt} invocation.
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
