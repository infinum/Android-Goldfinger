package co.infinum.goldfinger;

class MissingHardwareException extends Exception {

    MissingHardwareException() {
        super("Device has no fingerprint hardware.");
    }
}
