package co.infinum.goldfinger;

import androidx.annotation.NonNull;

/**
 * Legacy implementation for pre-Marshmallow devices.
 */
class LegacyGoldfinger implements Goldfinger {

    @Override
    public void authenticate(@NonNull Callback callback) {
    }

    @Override
    public void cancel() {
    }

    @Override
    public void decrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback) {
    }

    @Override
    public void encrypt(@NonNull String keyName, @NonNull String value, @NonNull Callback callback) {
    }

    @Override
    public boolean hasEnrolledFingerprint() {
        return false;
    }

    @Override
    public boolean hasFingerprintHardware() {
        return false;
    }
}
