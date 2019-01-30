package co.infinum.goldfinger;

import androidx.annotation.NonNull;

/**
 * Legacy implementation for pre-Marshmallow devices.
 */
class LegacyGoldfinger implements Goldfinger {

    @Override
    public void authenticate(@NonNull GoldfingerParams params, @NonNull GoldfingerCallback callback) {
    }

    @Override
    public void cancel() {
    }

    @Override
    public void decrypt(@NonNull GoldfingerParams params, @NonNull GoldfingerCallback callback) {
    }

    @Override
    public void encrypt(@NonNull GoldfingerParams params, @NonNull GoldfingerCallback callback) {
    }

    @Override
    public boolean hasFingerprintHardware() {
        return false;
    }
}
