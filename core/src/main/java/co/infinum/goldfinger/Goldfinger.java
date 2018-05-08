package co.infinum.goldfinger;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

public interface Goldfinger {

    /**
     * Returns true if device has fingerprint hardware, false otherwise.
     */
    boolean hasFingerprintHardware();

    /**
     * Returns true if user has fingerprint in device settings, false otherwise.
     */
    boolean hasEnrolledFingerprint();

    /**
     * Authenticate user via Fingerprint.
     * <p>
     * Example - Process payment after successful fingerprint authentication.
     */
    void authenticate(Callback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link Crypto} implementation is used to automatically decrypt given value.
     * <p>
     * Should be used together with {@link Goldfinger#encrypt(String, String, Callback)} to decrypt saved data.
     *
     * @param keyName unique key identifier, {@link java.security.Key} saved under this value is loaded from {@link java.security.KeyStore}
     * @param value   String value which will be decrypted if user successfully authenticates
     */
    void decrypt(String keyName, String value, Callback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link Crypto} implementation is used to automatically encrypt given value.
     * <p>
     * Use it when saving some data that should not be saved as plain text (e.g. password).
     * To decrypt the value use {@link Goldfinger#decrypt(String, String, Callback)} method.
     * <p>
     * Example - Allow auto-login via Fingerprint.
     *
     * @param keyName unique key identifier, {@link java.security.Key} is stored to {@link java.security.KeyStore} under this value
     * @param value   String value which will be encrypted if user successfully authenticates
     */
    void encrypt(String keyName, String value, Callback callback);

    /**
     * Cancel current active Fingerprint authentication.
     */
    void cancel();

    /**
     * Become Bob the builder.
     */
    class Builder {

        private final Context context;
        private Crypto crypto;
        private CryptoFactory cryptoFactory;

        public Builder(Context context) {
            this.context = context;
        }

        public Goldfinger build() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return buildMarshmallowInstance();
            } else {
                return new LegacyGoldfinger();
            }
        }

        public Builder setCrypto(Crypto crypto) {
            this.crypto = crypto;
            return this;
        }

        public Builder setCryptoFactory(CryptoFactory cryptoFactory) {
            this.cryptoFactory = cryptoFactory;
            return this;
        }

        public Builder setLogEnabled(boolean logEnabled) {
            LogUtils.setEnabled(logEnabled);
            return this;
        }

        @RequiresApi(Build.VERSION_CODES.M)
        private Goldfinger buildMarshmallowInstance() {
            Crypto finalCrypto = crypto != null ? crypto : new Crypto.Default();
            CryptoFactory finalCryptoFactory =
                cryptoFactory != null ? cryptoFactory : new CryptoFactory.Default(context);
            AsyncCryptoFactory asyncCryptoFactory = new AsyncCryptoFactory(finalCryptoFactory);
            return new MarshmallowGoldfinger(context, asyncCryptoFactory, finalCrypto);
        }
    }

    abstract class Callback {

        /**
         * User successfully authenticated.
         *
         * @param value This value can be one of:
         *              1) Empty string - if {@link #authenticate(Callback)} is used
         *              2) Encrypted string - if {@link #encrypt(String, String, Callback)} is used
         *              3) Decrypted string - if {@link #decrypt(String, String, Callback)} is used
         */
        abstract public void onSuccess(String value);

        /**
         * @see Error
         */
        abstract public void onError(Error error);

        /**
         * Callback is dispatched after {@link android.support.v4.hardware.fingerprint.FingerprintManagerCompat.CryptoObject} is
         * initialized and just before Fingerprint authentication is started.
         * <p>
         * Example - You want to display Dialog only if initialization is successful.
         */
        public void onReady() {
        }
    }
}
