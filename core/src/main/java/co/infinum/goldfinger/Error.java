package co.infinum.goldfinger;

import androidx.biometric.BiometricPrompt;

/**
 * Wraps all possible errors that can happen.
 * <p>
 * It's important to differentiate critical errors from non-critical errors.
 */
public enum Error {

    /**
     * @see BiometricPrompt#ERROR_HW_UNAVAILABLE
     */
    HARDWARE_UNAVAILABLE(false),

    /**
     * @see BiometricPrompt#ERROR_UNABLE_TO_PROCESS
     */
    UNABLE_TO_PROCESS(false),

    /**
     * @see BiometricPrompt#ERROR_TIMEOUT
     */
    TIMEOUT(false),

    /**
     * @see BiometricPrompt#ERROR_NO_SPACE
     */
    NO_SPACE(false),

    /**
     * @see BiometricPrompt#ERROR_CANCELED
     */
    CANCELED(false),

    /**
     * @see BiometricPrompt#ERROR_LOCKOUT
     */
    LOCKOUT(false),

    /**
     * @see BiometricPrompt#ERROR_VENDOR
     */
    VENDOR(false),

    /**
     * @see BiometricPrompt#ERROR_LOCKOUT_PERMANENT
     */
    LOCKOUT_PERMANENT(false),

    /**
     * @see BiometricPrompt#ERROR_USER_CANCELED
     */
    USER_CANCELED(false),

    /**
     * @see BiometricPrompt#ERROR_NO_BIOMETRICS
     */
    NO_BIOMETRICS(false),

    /**
     * @see BiometricPrompt#ERROR_HW_NOT_PRESENT
     */
    HARDWARE_NOT_PRESENT(false),

    /**
     * @see BiometricPrompt#ERROR_NEGATIVE_BUTTON
     */
    NEGATIVE_BUTTON(false),

    /**
     * Value decryption failed.
     */
    DECRYPTION_FAILED(true),

    /**
     * Value encryption failed.
     */
    ENCRYPTION_FAILED(true),

    /**
     * CryptoObject creation failed.
     */
    CRYPTO_OBJECT_CREATE_FAILED(true),

    /**
     * Params are invalid, logs contain detailed explanation what is missing.
     * <p>
     * All methods expect GoldfingerParams to contain title and negativeButtonText. BiometricsPrompt API
     * throws exception if they are not provided.
     * <p>
     * Encryption and decryption calls must have valid {@link CryptographyData}.
     */
    INVALID_PARAMS(false),

    /**
     * Unknown error happened.
     */
    UNKNOWN(false);

    private boolean invalidateFingerprint;

    Error(boolean invalidateFingerprint) {
        this.invalidateFingerprint = invalidateFingerprint;
    }

    static Error fromBiometricError(int id) {
        switch (id) {
            case BiometricPrompt.ERROR_HW_UNAVAILABLE:
                return HARDWARE_UNAVAILABLE;
            case BiometricPrompt.ERROR_UNABLE_TO_PROCESS:
                return UNABLE_TO_PROCESS;
            case BiometricPrompt.ERROR_TIMEOUT:
                return TIMEOUT;
            case BiometricPrompt.ERROR_NO_SPACE:
                return NO_SPACE;
            case BiometricPrompt.ERROR_CANCELED:
                return CANCELED;
            case BiometricPrompt.ERROR_LOCKOUT:
                return LOCKOUT;
            case BiometricPrompt.ERROR_VENDOR:
                return VENDOR;
            case BiometricPrompt.ERROR_LOCKOUT_PERMANENT:
                return LOCKOUT_PERMANENT;
            case BiometricPrompt.ERROR_USER_CANCELED:
                return USER_CANCELED;
            case BiometricPrompt.ERROR_NO_BIOMETRICS:
                return NO_BIOMETRICS;
            case BiometricPrompt.ERROR_HW_NOT_PRESENT:
                return HARDWARE_NOT_PRESENT;
            case BiometricPrompt.ERROR_NEGATIVE_BUTTON:
                return NEGATIVE_BUTTON;
            default:
                return UNKNOWN;
        }
    }

    /**
     * Some errors are more serious than others. When specific errors are
     * received, it means that existing user's fingerprint can't and should
     * not be used but he should add new fingerprint.
     *
     * @return boolean value to know whether or not you should invalidate user's
     * existing fingerprint
     */
    public boolean shouldInvalidateFingerprint() {
        return invalidateFingerprint;
    }
}
