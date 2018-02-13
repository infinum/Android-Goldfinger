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
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link Crypto} implementation is used to automatically decrypt given value.
     *
     * @param keyName unique key identifier, {@link java.security.Key} saved under this value is loaded from {@link java.security.KeyStore}
     * @param value   String value which will be decrypted if user successfully authenticates
     */
    void decrypt(String keyName, String value, Callback callback);

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * {@link Crypto} implementation is used to automatically encrypt given value.
     *
     * @param keyName unique key identifier, {@link java.security.Key} is stored to {@link java.security.KeyStore} under this value
     * @param value   String value which will be encrypted if user successfully authenticates
     */
    void encrypt(String keyName, String value, Callback callback);

    /**
     * Cancel current active Fingerprint authentication.
     */
    void cancel();

    interface Callback {

        /**
         * User successfully authenticated.
         *
         * @param value This value can be one of:
         *              1) Empty string - if {@link #authenticate(Callback)} is used
         *              2) Encrypted string - if {@link #encrypt(String, String, Callback)} is used
         *              3) Decrypted string - if {@link #decrypt(String, String, Callback)} is used
         */
        void onSuccess(String value);

        /**
         * Authentication failed but authentication is still active
         * and user can retry fingerprint authentication.
         *
         * @see Warning
         */
        void onWarning(Warning warning);

        /**
         * Authentication or initialization error happened and fingerprint authentication
         * is not active.
         *
         * @see Error
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
            AsyncCryptoFactory asyncCryptoFactory = new AsyncCryptoFactory(finalCryptoFactory);
            return new MarshmallowGoldfinger(context, asyncCryptoFactory, finalCrypto, logger);
        }
    }
}
