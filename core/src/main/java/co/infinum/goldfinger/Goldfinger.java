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
import co.infinum.goldfinger.crypto.CipherCrypter;
import co.infinum.goldfinger.crypto.CipherFactory;
import co.infinum.goldfinger.crypto.MacCrypter;
import co.infinum.goldfinger.crypto.MacFactory;
import co.infinum.goldfinger.crypto.SignatureCrypter;
import co.infinum.goldfinger.crypto.SignatureFactory;
import co.infinum.goldfinger.crypto.impl.AesCipherFactory;
import co.infinum.goldfinger.crypto.impl.Base64CipherCrypter;

@SuppressWarnings("unused")
public interface Goldfinger {

    /**
     * @deprecated Use {@link #hasFingerprintHardware(int)} instead.
     */
    @Deprecated
    boolean hasFingerprintHardware();

    /**
     * @deprecated Use {@link #hasBiometricHardware(int)} instead.
     */
    @Deprecated
    boolean hasFingerprintHardware(int authenticators);

    /**
     * Returns true if user has biometrics hardware, false otherwise.
     */
    boolean hasBiometricHardware(int authenticators);

    /**
     * @deprecated Use {@link #hasEnrolledFingerprint(int)} instead.
     */
    @Deprecated
    boolean hasEnrolledFingerprint();

    /**
     * @deprecated Use {@link #hasEnrolledBiometrics(int)} instead.
     */
    @Deprecated
    boolean hasEnrolledFingerprint(int authenticators);

    /**
     * Returns true if user has enrolled biometrics, false otherwise.
     */
    boolean hasEnrolledBiometrics(int authenticators);

    /**
     * @deprecated Use {@link #canAuthenticate(int)} instead.
     */
    @Deprecated
    boolean canAuthenticate();

    /**
     * @see BiometricManager#canAuthenticate(int)
     */
    boolean canAuthenticate(int authenticators);

    /**
     * Authenticate user via Biometrics.
     * <p>
     * Example - Process payment after successful biometric authentication.
     *
     * @see PromptParams
     */
    void authenticate(@NonNull PromptParams params, @NonNull Callback callback);

    /**
     * Authenticate user via Biometrics. If user is successfully authenticated,
     * {@link CrypterProxy} implementation is used to automatically encrypt given value.
     * <p>
     * Use it when saving some data that should not be saved as plain text (e.g. password).
     * To decrypt the value use {@link Goldfinger#decrypt} method.
     * <p>
     * Example - Allow auto-login via Biometrics.
     *
     * @param params   parameters used to build {@link BiometricPrompt} instance
     * @param key      unique key identifier, used to store cipher IV internally
     * @param value    String value which will be encrypted if user successfully authenticates
     * @param callback callback
     * @see Goldfinger.Callback
     */
    void encrypt(
        @NonNull PromptParams params,
        @NonNull String key,
        @NonNull String value,
        @NonNull Callback callback
    );

    /**
     * Authenticate user via Biometrics. If user is successfully authenticated,
     * {@link CrypterProxy} implementation is used to automatically decrypt given value.
     * <p>
     * Should be used together with {@link Goldfinger#encrypt} to decrypt saved data.
     *
     * @param key   unique key identifier, used to load Cipher IV internally
     * @param value String value which will be decrypted if user successfully authenticates
     */
    void decrypt(
        @NonNull PromptParams params,
        @NonNull String key,
        @NonNull String value,
        @NonNull Callback callback
    );

    /**
     * Cancel current active Biometrics authentication.
     */
    void cancel();

    /**
     * Become Bob the builder.
     */
    @SuppressWarnings("UnusedReturnValue")
    class Builder {

        @NonNull private final Context context;
        @Nullable private CipherFactory cipherFactory;
        @Nullable private MacFactory macFactory;
        @Nullable private SignatureFactory signatureFactory;
        @Nullable private CipherCrypter cipherCrypter;
        @Nullable private MacCrypter macCrypter;
        @Nullable private SignatureCrypter signatureCrypter;
        @Nullable private String key;
        @Nullable private String value;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        public Goldfinger build() {
            ensureParamsValid();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return buildGoldfinger();
            } else {
                return new GoldfingerMock();
            }
        }

        @NonNull
        public Builder cipherCrypter(@Nullable CipherCrypter cipherCrypter) {
            this.cipherCrypter = cipherCrypter;
            return this;
        }

        @NonNull
        public Builder cipherFactory(@Nullable CipherFactory cipherFactory) {
            this.cipherFactory = cipherFactory;
            return this;
        }

        @NonNull
        public Builder logEnabled(boolean logEnabled) {
            LogUtils.setEnabled(logEnabled);
            return this;
        }

        @NonNull
        public Builder macCrypter(@Nullable MacCrypter macCrypter) {
            this.macCrypter = macCrypter;
            return this;
        }

        @NonNull
        public Builder macFactory(@Nullable MacFactory macFactory) {
            this.macFactory = macFactory;
            return this;
        }

        @NonNull
        public Builder signatureCrypter(@Nullable SignatureCrypter signatureCrypter) {
            this.signatureCrypter = signatureCrypter;
            return this;
        }

        @NonNull
        public Builder signatureFactory(@Nullable SignatureFactory signatureFactory) {
            this.signatureFactory = signatureFactory;
            return this;
        }

        @NonNull
        @RequiresApi(Build.VERSION_CODES.M)
        private Goldfinger buildGoldfinger() {
            if (macCrypter == null && signatureCrypter == null && cipherCrypter == null) {
                this.cipherCrypter = new Base64CipherCrypter();
            }
            if (macFactory == null && signatureFactory == null && cipherFactory == null) {
                this.cipherFactory = new AesCipherFactory(context);
            }
            AsyncCryptoObjectFactory asyncFactory = new AsyncCryptoObjectFactory(
                new CryptoObjectFactory(cipherFactory, macFactory, signatureFactory)
            );
            CrypterProxy cryptoProxy = new CrypterProxy(cipherCrypter, macCrypter, signatureCrypter);

            return new GoldfingerImpl(context, asyncFactory, cryptoProxy);
        }

        private void ensureParamsValid() {
            if (macFactory != null && macCrypter == null || macFactory == null && macCrypter != null) {
                throw new RuntimeException(
                    "To use CryptoObject with MacObject you must provide both MacFactory and "
                        + "MacCrypter implementation. Use Goldfinger.Builder#macFactory(MacFactory) and "
                        + "Goldfinger.Builder#macCrypter(MacCrypter) methods to set values."
                );
            }

            if (signatureFactory != null && signatureCrypter == null || signatureFactory == null && signatureCrypter != null) {
                throw new RuntimeException(
                    "To use CryptoObject with SignatureObject you must provide both SignatureFactory and "
                        + "SignatureCrypter implementation. Use Goldfinger.Builder#signatureFactory(SignatureFactory) and "
                        + "Goldfinger.Builder#signatureCrypter(SignatureCrypter) methods to set values."
                );
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class PromptParams {

        @NonNull private final Object dialogOwner;
        @Nullable private final String description;
        @Nullable private final String negativeButtonText;
        @Nullable private final String subtitle;
        @Nullable private final String title;
        private final boolean confirmationRequired;
        private final boolean deviceCredentialsAllowed;
        private final int allowedAuthenticators;

        private PromptParams(
            @NonNull Object dialogOwner,
            @Nullable String title,
            @Nullable String description,
            @Nullable String negativeButtonText,
            @Nullable String subtitle,
            boolean confirmationRequired,
            boolean deviceCredentialsAllowed,
            int allowedAuthenticators
        ) {
            this.dialogOwner = dialogOwner;
            this.title = title;
            this.description = description;
            this.negativeButtonText = negativeButtonText;
            this.subtitle = subtitle;
            this.confirmationRequired = confirmationRequired;
            this.deviceCredentialsAllowed = deviceCredentialsAllowed;
            this.allowedAuthenticators = allowedAuthenticators;
        }

        @NonNull
        public Object dialogOwner() {
            return dialogOwner;
        }

        @Nullable
        public String title() {
            return title;
        }

        @Nullable
        public String description() {
            return description;
        }

        @Nullable
        public String negativeButtonText() {
            return negativeButtonText;
        }

        @Nullable
        public String subtitle() {
            return subtitle;
        }

        public boolean confirmationRequired() {
            return confirmationRequired;
        }

        public boolean deviceCredentialsAllowed() {
            return deviceCredentialsAllowed;
        }

        public int allowedAuthenticators() {
            return allowedAuthenticators;
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
                .setAllowedAuthenticators(allowedAuthenticators)
                .setNegativeButtonText(negativeButtonText)
                .setConfirmationRequired(confirmationRequired);

            return builder.build();
        }

        public static class Builder {

            /* Dialog dialogOwner can be either Fragment or FragmentActivity */
            @NonNull private Object dialogOwner;
            @Nullable private String description;
            @Nullable private String negativeButtonText;
            @Nullable private String subtitle;
            @Nullable private String title;
            private boolean confirmationRequired;
            private int allowedAuthenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK;

            public Builder(@NonNull FragmentActivity activity) {
                this.dialogOwner = activity;
            }

            public Builder(@NonNull Fragment fragment) {
                this.dialogOwner = fragment;
            }

            @NonNull
            public PromptParams build() {
                boolean deviceCredentialAllowed = (allowedAuthenticators & BiometricManager.Authenticators.DEVICE_CREDENTIAL) != 0;

                return new PromptParams(
                    dialogOwner,
                    title,
                    description,
                    negativeButtonText,
                    subtitle,
                    confirmationRequired,
                    deviceCredentialAllowed,
                    allowedAuthenticators
                );
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
             * @see BiometricPrompt.PromptInfo.Builder#setConfirmationRequired
             */
            @NonNull
            public Builder confirmationRequired(boolean confirmationRequired) {
                this.confirmationRequired = confirmationRequired;
                return this;
            }

            /**
             * @see BiometricPrompt.PromptInfo.Builder#setAllowedAuthenticators(int)
             */
            @NonNull
            public Builder allowedAuthenticators(int allowedAuthenticators) {
                this.allowedAuthenticators = allowedAuthenticators;
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
     * biometric authentication and value for encryption/decryption operations.
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
         * IFF {@link Goldfinger#encrypt} or {@link Goldfinger#decrypt}
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

    /**
     * Callback used to receive Goldfinger results.
     */
    interface Callback {

        /**
         * Returns biometric result and will be invoked multiple times during
         * biometric authentication as not all biometric results complete
         * the authentication.
         * <p>
         * Result callback invoked for every biometric result (success, error or info).
         * It can be invoked multiple times during single biometric authentication.
         *
         * @param result contains biometric result information
         * @see Goldfinger.Result
         */
        void onResult(@NonNull Goldfinger.Result result);

        /**
         * Critical error happened and biometric authentication is stopped.
         */
        void onError(@NonNull Exception e);
    }

    /**
     * Describes in detail why {@link Callback#onResult} is dispatched.
     */
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
         * @see BiometricPrompt#ERROR_SECURITY_UPDATE_REQUIRED
         */
        SECURITY_UPDATE_REQUIRED,

        /**
         * Dispatched when Biometrics authentication is initialized correctly and
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

    /**
     * Describes the type of the result received in {@link Callback#onResult}
     */
    enum Type {

        /**
         * Biometrics authentication is successfully finished. {@link Goldfinger.Result}
         * will contain value in case of {@link PromptParams.Builder#decrypt} or
         * {@link PromptParams.Builder#encrypt} invocation.
         */
        SUCCESS,

        /**
         * Biometrics authentication is still active. {@link Goldfinger.Result} contains
         * additional information about currently active biometric authentication.
         */
        INFO,

        /**
         * Biometrics authentication is unsuccessfully finished. {@link Goldfinger.Result}
         * contains the reason why the authentication failed.
         */
        ERROR
    }
}
