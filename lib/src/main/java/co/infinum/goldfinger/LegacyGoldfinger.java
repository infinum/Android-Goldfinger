package co.infinum.goldfinger;

/**
 * Legacy implementation for pre-Marshmallow devices.
 */
class LegacyGoldfinger implements Goldfinger {

    @Override
    public boolean hasFingerprintHardware() {
        return false;
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        return false;
    }

    @Override
    public void authenticate(Callback callback) {
    }

    @Override
    public void decrypt(String keyName, String value, Callback callback) {
    }

    @Override
    public void encrypt(String keyName, String value, Callback callback) {
    }

    @Override
    public void cancel() {
    }
}
