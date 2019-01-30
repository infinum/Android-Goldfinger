package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

public interface Goldfinger {

    /**
     * Returns true if device has fingerprint hardware, false otherwise.
     */
    boolean hasFingerprintHardware();

    /**
     * Authenticate user via Fingerprint.
     * <p>
     * Example - Process payment after successful fingerprint authentication.
     *
     * @see GoldfingerParams
     */
    void authenticate(@NonNull GoldfingerParams params, @NonNull GoldfingerCallback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link CryptographyHandler} implementation is used to automatically decrypt given value.
     *
     * @see GoldfingerParams
     */
    void decrypt(@NonNull GoldfingerParams params, @NonNull GoldfingerCallback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link CryptographyHandler} implementation is used to automatically encrypt given value.
     * <p>
     * Use it when saving some data that should not be saved as plain text (e.g. password).
     * To decrypt the value use {@link Goldfinger#decrypt(GoldfingerParams, GoldfingerCallback)}} method.
     *
     * @see GoldfingerParams
     * @see GoldfingerCallback
     */
    void encrypt(@NonNull GoldfingerParams params, @NonNull GoldfingerCallback callback);

    /**
     * Cancel current active Fingerprint authentication.
     */
    void cancel();

    /**
     * Become Bob the builder.
     */
    @SuppressWarnings("unused")
    class Builder {

        private final Context context;
        private CryptoObjectFactory cryptoObjectFactory;
        private CryptographyHandler cryptographyHandler;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        public Goldfinger build() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return buildMarshmallowInstance();
            } else {
                return new LegacyGoldfinger();
            }
        }

        @NonNull
        public Builder cryptoObjectFactory(@NonNull CryptoObjectFactory cryptoObjectFactory) {
            this.cryptoObjectFactory = cryptoObjectFactory;
            return this;
        }

        @NonNull
        public Builder cryptographyHandler(@NonNull CryptographyHandler cryptographyHandler) {
            this.cryptographyHandler = cryptographyHandler;
            return this;
        }

        @NonNull
        public Builder logEnabled(boolean logEnabled) {
            LogUtils.setEnabled(logEnabled);
            return this;
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @NonNull
        private Goldfinger buildMarshmallowInstance() {
            CryptographyHandler handler = cryptographyHandler != null ? cryptographyHandler : new CryptographyHandler.Default();
            CryptoObjectFactory factory = cryptoObjectFactory != null ? cryptoObjectFactory : new CryptoObjectFactory.Default(context);
            AsyncCryptoObjectFactory asyncFactory = new AsyncCryptoObjectFactory(factory);
            return new MarshmallowGoldfinger(context, asyncFactory, handler);
        }
    }
}
