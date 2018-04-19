package co.infinum.goldfinger;

import android.hardware.fingerprint.FingerprintManager;

public enum Error {
    /**
     * The hardware is unavailable.
     */
    UNAVAILABLE,
    /**
     * Error state returned when the sensor was unable to process the current image.
     */
    UNABLE_TO_PROCESS,
    /**
     * Error state returned when the current request has been running too long.
     */
    TIMEOUT,
    /**
     * Error state returned for operations like enrollment; the operation cannot be completed because there's not
     * enough storage remaining to complete the operation.
     */
    NOT_ENOUGH_SPACE,
    /**
     * The operation was canceled because the fingerprint sensor is unavailable.
     */
    CANCELED,
    /**
     * The operation was canceled because the API is locked out due to too many attempts.
     */
    LOCKOUT,
    /**
     * CryptoFactory failed to initialize CryptoObject.
     */
    CRYPTO_OBJECT_INIT,
    /**
     * Crypto failed to decrypt the value.
     */
    DECRYPTION_FAILED,
    /**
     * Crypto failed to encrypt the value.
     */
    ENCRYPTION_FAILED,
    /**
     * Unknown error happened.
     */
    UNKNOWN;

    static Error fromId(int id) {
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
                return LOCKOUT;
            default:
                return UNKNOWN;
        }
    }
}
