package co.infinum.goldfinger;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Wraps all possible errors that can happen.
 * <p>
 * It's important to differentiate critical errors from non-critical errors.
 *
 * @see Error#isCritical()
 */
public enum Error {

    /**
     * The hardware is unavailable.
     */
    UNAVAILABLE(true),

    /**
     * Error state returned when the sensor was unable to process the current image.
     */
    UNABLE_TO_PROCESS(true),

    /**
     * Error state returned when the current request has been running too long.
     */
    TIMEOUT(true),

    /**
     * Error state returned for operations like enrollment; the operation cannot be completed because there's not
     * enough storage remaining to complete the operation.
     */
    NOT_ENOUGH_SPACE(true),

    /**
     * The operation was canceled because the fingerprint sensor is unavailable.
     */
    CANCELED(true),

    /**
     * The operation was canceled because the API is locked out due to too many attempts.
     */
    LOCKOUT(true),

    /**
     * Fingerprint did not start due to initialization failure, probably because of
     * {@link android.security.keystore.KeyPermanentlyInvalidatedException}
     */
    INITIALIZATION_FAILED(true),

    /**
     * Crypto failed to decrypt the value.
     */
    DECRYPTION_FAILED(true),

    /**
     * Crypto failed to encrypt the value.
     */
    ENCRYPTION_FAILED(true),

    /**
     * User canceled fingerprint reading.
     */
    USER_CANCEL(false),

    /**
     * The image acquired was good.
     */
    GOOD(false),

    /**
     * Only a partial fingerprint image was detected.
     */
    PARTIAL(false),

    /**
     * The fingerprint image was too noisy to process due to a detected condition.
     */
    INSUFFICIENT(false),

    /**
     * The fingerprint image was too noisy due to suspected or detected dirt on the sensor.
     */
    DIRTY(false),

    /**
     * The fingerprint image was unreadable due to lack of motion.
     */
    TOO_SLOW(false),

    /**
     * The fingerprint image was incomplete due to quick motion.
     */
    TOO_FAST(false),

    /**
     * Fingerprint valid but not recognized.
     */
    FAILURE(false),

    /**
     * Unknown error happened.
     */
    UNKNOWN(true);

    private final boolean isCritical;

    Error(boolean isCritical) {
        this.isCritical = isCritical;
    }

    static Error fromFingerprintError(int id) {
        switch (id) {
            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                return UNAVAILABLE;
            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                return UNABLE_TO_PROCESS;
            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                return TIMEOUT;
            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                return NOT_ENOUGH_SPACE;
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                return CANCELED;
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT_PERMANENT:
                return LOCKOUT;
            case FingerprintManager.FINGERPRINT_ERROR_USER_CANCELED:
                return USER_CANCEL;
            default:
                return UNKNOWN;
        }
    }

    static Error fromFingerprintHelp(int id) {
        switch (id) {
            case FingerprintManager.FINGERPRINT_ACQUIRED_GOOD:
                return GOOD;
            case FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL:
                return PARTIAL;
            case FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT:
                return INSUFFICIENT;
            case FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY:
                return DIRTY;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW:
                return TOO_SLOW;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST:
                return TOO_FAST;
            default:
                return FAILURE;
        }
    }

    /**
     * If an error is critical, then Fingerprint authentication is not active anymore.
     * <p>
     * If an error is non-critical, then Fingerprint authentication did not succeed, but it
     * is still active and user can retry.
     */
    public boolean isCritical() {
        return isCritical;
    }
}
