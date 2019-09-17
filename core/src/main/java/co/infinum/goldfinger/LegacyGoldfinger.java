package co.infinum.goldfinger;

import androidx.annotation.NonNull;

/**
 * Legacy implementation for pre-Marshmallow devices.
 */
class LegacyGoldfinger implements Goldfinger {

    @Override
    public void authenticate(@NonNull Params params, @NonNull Callback callback) {
    }

    @Override
    public boolean canAuthenticate() {
        return false;
    }

    @Override
    public void cancel() {
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
