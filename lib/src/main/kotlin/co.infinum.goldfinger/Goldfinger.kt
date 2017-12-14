package co.infinum.goldfinger

import android.annotation.TargetApi
import android.content.Context
import android.os.Build

interface Goldfinger {
    companion object {
        fun create(context: Context) = Builder(context).build()
    }

    /**
     * Returns true if device has fingerprint hardware, false otherwise.
     */
    fun hasFingerprintHardware(): Boolean

    /**
     * Returns true if device has enabled lock screen, false otherwise.
     */
    fun hasEnabledLockScreen(): Boolean

    /**
     * Returns true if user has fingerprint in device settings, false otherwise.
     */
    fun hasEnrolledFingerprint(): Boolean

    /**
     * Authenticate user via Fingerprint.
     */
    fun authenticate(callback: Callback)

    /**
     * Authenticate user via Fingerprint, automatically load IV vector
     * from given keyName and decrypt given value.
     */
    fun decrypt(keyName: String, value: String, callback: Callback)

    /**
     * Authenticate user via Fingerprint, automatically save IV vector
     * to given keyName and encrypt given value.
     */
    fun encrypt(keyName: String, value: String, callback: Callback)

    /**
     * Cancel current active Fingerprint authentication.
     */
    fun cancel()

    /**
     * Become Bob the builder.
     */
    class Builder(private val context: Context) {

        private var cryptoCreator: CryptoCreator? = null
        private var crypto: Crypto? = null
        private var logger: Logger? = null

        fun logger(logger: Logger) = apply { this.logger = logger }
        fun cryptoCreator(cryptoCreator: CryptoCreator) = apply { this.cryptoCreator = cryptoCreator }
        fun crypto(crypto: Crypto) = apply { this.crypto = crypto }

        fun build() =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    buildMarshmallowGoldfinger()
                } else {
                    LegacyGoldfinger()
                }

        @TargetApi(Build.VERSION_CODES.M)
        private fun buildMarshmallowGoldfinger(): Goldfinger {
            val logger = this.logger ?: Logger.Default()
            val cryptoCreator = this.cryptoCreator ?: CryptoCreator.Default(context, logger)
            val crypto = this.crypto ?: Crypto.Default(logger)
            return MarshmallowGoldfinger(context, cryptoCreator, crypto, logger)
        }
    }

    interface Callback {

        /**
         * User successfully authenticated and value is encrypted/decrypted.
         * For authentication call, empty string is returned and value can be ignored.
         */
        fun onSuccess(value: String)

        /**
         * Authentication failed but authentication is still active
         * and user can retry fingerprint authentication.
         */
        fun onWarning(warning: Warning)

        /**
         * Authentication or initialization error happened and fingerprint authentication
         * is not active.
         */
        fun onError(error: Error)
    }
}