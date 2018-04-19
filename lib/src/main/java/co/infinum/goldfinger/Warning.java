package co.infinum.goldfinger;

import android.hardware.fingerprint.FingerprintManager;

public enum Warning {
    /**
     * The image acquired was good.
     */
    GOOD,
    /**
     * Only a partial fingerprint image was detected.
     */
    PARTIAL,
    /**
     * The fingerprint image was too noisy to process due to a detected condition.
     */
    INSUFFICIENT,
    /**
     * The fingerprint image was too noisy due to suspected or detected dirt on the sensor.
     */
    DIRTY,
    /**
     * The fingerprint image was unreadable due to lack of motion.
     */
    TOO_SLOW,
    /**
     * The fingerprint image was incomplete due to quick motion.
     */
    TOO_FAST,
    /**
     * Fingerprint valid but not recognized.
     */
    FAILURE;

    static Warning fromId(int id) {
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
}
