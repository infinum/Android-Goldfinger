package co.infinum.goldfinger;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Internal class used to convert Fingerprint IDs into meaningful enum.
 */
class EnumConverter {

    private EnumConverter() {
    }

    static Goldfinger.Reason errorToReason(int errorId) {
        switch (errorId) {
            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                return Goldfinger.Reason.HARDWARE_UNAVAILABLE;
            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                return Goldfinger.Reason.UNABLE_TO_PROCESS;
            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                return Goldfinger.Reason.TIMEOUT;
            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                return Goldfinger.Reason.NO_SPACE;
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                return Goldfinger.Reason.CANCELED;
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
                return Goldfinger.Reason.LOCKOUT;
            case FingerprintManager.FINGERPRINT_ERROR_VENDOR:
                return Goldfinger.Reason.VENDOR;
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT_PERMANENT:
                return Goldfinger.Reason.LOCKOUT_PERMANENT;
            case FingerprintManager.FINGERPRINT_ERROR_USER_CANCELED:
                return Goldfinger.Reason.USER_CANCELED;
            default:
                return Goldfinger.Reason.UNKNOWN;
        }
    }

    static Goldfinger.Reason helpToReason(int helpId) {
        switch (helpId) {
            case FingerprintManager.FINGERPRINT_ACQUIRED_GOOD:
                return Goldfinger.Reason.GOOD;
            case FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL:
                return Goldfinger.Reason.PARTIAL;
            case FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT:
                return Goldfinger.Reason.INSUFFICIENT;
            case FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY:
                return Goldfinger.Reason.IMAGER_DIRTY;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW:
                return Goldfinger.Reason.TOO_SLOW;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST:
                return Goldfinger.Reason.TOO_FAST;
            default:
                return Goldfinger.Reason.UNKNOWN;
        }
    }
}
