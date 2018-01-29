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
     */
    void authenticate(Callback callback);

    /**
     * Authenticate user via Fingerprint, automatically load IV vector
     * from given keyName and decrypt given value.
     */
    void decrypt(String keyName, String value, Callback callback);

    /**
     * Authenticate user via Fingerprint, automatically save IV vector
     * to given keyName and encrypt given value.
     */
    void encrypt(String keyName, String value, Callback callback);

    /**
     * Cancel current active Fingerprint authentication.
     */
    void cancel();

    interface Callback {

        /**
         * User successfully authenticated and value is encrypted/decrypted.
         * For authentication call, empty string is returned and value can be ignored.
         */
        void onSuccess(String value);

        /**
         * Authentication failed but authentication is still active
         * and user can retry fingerprint authentication.
         */
        void onWarning(Warning warning);

        /**
         * Authentication or initialization error happened and fingerprint authentication
         * is not active.
         */
        void onError(Error error);
    }

    /**
     * Become Bob the builder.
     */
    class Builder {

        private final Context context;
        private CryptoFactory cryptoFactory;
        private Crypto crypto;
        private boolean logEnabled = false;

        public Builder(Context context) {
            this.context = context;
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
            this.logEnabled = logEnabled;
            return this;
        }

        public Goldfinger build() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return buildMarshmallowInstance();
            } else {
                return new LegacyGoldfinger();
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        private Goldfinger buildMarshmallowInstance() {
            Logger logger = new Logger(logEnabled);
            Crypto finalCrypto = crypto != null ? crypto : new Crypto.Default(logger);
            CryptoFactory finalCryptoFactory =
                    cryptoFactory != null ? cryptoFactory : new CryptoFactory.Default(context, logger);
            return new MarshmallowGoldfinger(context, finalCryptoFactory, finalCrypto, logger);
        }
    }
}
