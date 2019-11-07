package co.infinum.goldfinger;

import androidx.annotation.NonNull;

/**
 * Legacy implementation for pre-Marshmallow devices.
 */
class GoldfingerMock implements Goldfinger {

    @Override
    public void authenticate(@NonNull PromptParams params, @NonNull Callback callback) {
    }

    @Override
    public boolean canAuthenticate() {
        return false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void decrypt(@NonNull PromptParams params, @NonNull String key, @NonNull String value, @NonNull Callback callback) {
    }

    @Override
    public void encrypt(@NonNull PromptParams params, @NonNull String key, @NonNull String value, @NonNull Callback callback) {
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
