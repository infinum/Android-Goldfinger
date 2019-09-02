package co.infinum.goldfinger;

/**
 * Thrown if the device is missing fingerprint authentication hardware.
 */
class MissingHardwareException extends Exception {

    MissingHardwareException() {
        super("Device has no fingerprint hardware.");
    }
}
