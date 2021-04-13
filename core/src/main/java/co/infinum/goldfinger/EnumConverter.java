package co.infinum.goldfinger;

import androidx.biometric.BiometricPrompt;

/**
 * Internal class used to convert Biometric constants into meaningful enum.
 */
class EnumConverter {

    private EnumConverter() {
    }

    static Goldfinger.Reason errorToReason(int errorId) {
        switch (errorId) {
            case BiometricPrompt.ERROR_HW_UNAVAILABLE:
                return Goldfinger.Reason.HARDWARE_UNAVAILABLE;
            case BiometricPrompt.ERROR_UNABLE_TO_PROCESS:
                return Goldfinger.Reason.UNABLE_TO_PROCESS;
            case BiometricPrompt.ERROR_TIMEOUT:
                return Goldfinger.Reason.TIMEOUT;
            case BiometricPrompt.ERROR_NO_SPACE:
                return Goldfinger.Reason.NO_SPACE;
            case BiometricPrompt.ERROR_CANCELED:
                return Goldfinger.Reason.CANCELED;
            case BiometricPrompt.ERROR_LOCKOUT:
                return Goldfinger.Reason.LOCKOUT;
            case BiometricPrompt.ERROR_VENDOR:
                return Goldfinger.Reason.VENDOR;
            case BiometricPrompt.ERROR_LOCKOUT_PERMANENT:
                return Goldfinger.Reason.LOCKOUT_PERMANENT;
            case BiometricPrompt.ERROR_USER_CANCELED:
                return Goldfinger.Reason.USER_CANCELED;
            case BiometricPrompt.ERROR_NO_BIOMETRICS:
                return Goldfinger.Reason.NO_BIOMETRICS;
            case BiometricPrompt.ERROR_HW_NOT_PRESENT:
                return Goldfinger.Reason.HW_NOT_PRESENT;
            case BiometricPrompt.ERROR_NEGATIVE_BUTTON:
                return Goldfinger.Reason.NEGATIVE_BUTTON;
            case BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL:
                return Goldfinger.Reason.NO_DEVICE_CREDENTIAL;
            case BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED:
                return Goldfinger.Reason.SECURITY_UPDATE_REQUIRED;
            default:
                return Goldfinger.Reason.UNKNOWN;
        }
    }
}
