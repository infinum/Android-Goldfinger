package co.infinum.goldfinger;

import androidx.biometric.BiometricConstants;

/**
 * Internal class used to convert Biometric constants into meaningful enum.
 */
class EnumConverter {

    private EnumConverter() {
    }

    static Goldfinger.Reason errorToReason(int errorId) {
        switch (errorId) {
            case BiometricConstants.ERROR_HW_UNAVAILABLE:
                return Goldfinger.Reason.HARDWARE_UNAVAILABLE;
            case BiometricConstants.ERROR_UNABLE_TO_PROCESS:
                return Goldfinger.Reason.UNABLE_TO_PROCESS;
            case BiometricConstants.ERROR_TIMEOUT:
                return Goldfinger.Reason.TIMEOUT;
            case BiometricConstants.ERROR_NO_SPACE:
                return Goldfinger.Reason.NO_SPACE;
            case BiometricConstants.ERROR_CANCELED:
                return Goldfinger.Reason.CANCELED;
            case BiometricConstants.ERROR_LOCKOUT:
                return Goldfinger.Reason.LOCKOUT;
            case BiometricConstants.ERROR_VENDOR:
                return Goldfinger.Reason.VENDOR;
            case BiometricConstants.ERROR_LOCKOUT_PERMANENT:
                return Goldfinger.Reason.LOCKOUT_PERMANENT;
            case BiometricConstants.ERROR_USER_CANCELED:
                return Goldfinger.Reason.USER_CANCELED;
            case BiometricConstants.ERROR_NO_BIOMETRICS:
                return Goldfinger.Reason.NO_BIOMETRICS;
            case BiometricConstants.ERROR_HW_NOT_PRESENT:
                return Goldfinger.Reason.HW_NOT_PRESENT;
            case BiometricConstants.ERROR_NEGATIVE_BUTTON:
                return Goldfinger.Reason.NEGATIVE_BUTTON;
            case BiometricConstants.ERROR_NO_DEVICE_CREDENTIAL:
                return Goldfinger.Reason.NO_DEVICE_CREDENTIAL;
            default:
                return Goldfinger.Reason.UNKNOWN;
        }
    }
}
