package co.infinum.goldfinger;

/**
 * Thrown if the device is missing biometric authentication hardware.
 */
@SuppressWarnings("WeakerAccess")
public class MissingHardwareException extends Exception {

    MissingHardwareException() {
        super("Device has no biometric hardware.");
    }
}
