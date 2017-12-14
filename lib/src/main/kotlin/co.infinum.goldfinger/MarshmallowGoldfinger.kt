package co.infinum.goldfinger

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat

@RequiresApi(Build.VERSION_CODES.M)
internal class MarshmallowGoldfinger constructor(
        context: Context,
        private val cryptoCreator: CryptoCreator,
        private val crypto: Crypto,
        private val logger: Logger
) : Goldfinger {

    private val fingerprintManagerCompat = FingerprintManagerCompat.from(context)
    private val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
    private var fingerprintCallback: CancellableAuthenticationCallback? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun hasFingerprintHardware() = fingerprintManagerCompat.isHardwareDetected

    override fun hasEnrolledFingerprint() = fingerprintManagerCompat.hasEnrolledFingerprints()

    override fun hasEnabledLockScreen() = keyguardManager?.isKeyguardSecure == true

    override fun authenticate(callback: Goldfinger.Callback) =
            startFingerprintAuthentication(keyName = "<Goldfinger authentication mode>", mode = Mode.AUTHENTICATE, callback = callback)

    override fun decrypt(keyName: String, value: String, callback: Goldfinger.Callback) =
            startFingerprintAuthentication(keyName, value, Mode.DECRYPTION, callback)

    override fun encrypt(keyName: String, value: String, callback: Goldfinger.Callback) =
            startFingerprintAuthentication(keyName, value, Mode.ENCRYPTION, callback)

    private fun startFingerprintAuthentication(keyName: String = "", value: String = "", mode: Mode, callback: Goldfinger.Callback) {
        cancel()
        val cryptoObject = when (mode) {
            Mode.AUTHENTICATE -> cryptoCreator.createAuthenticationCryptoObject(keyName)
            Mode.ENCRYPTION -> cryptoCreator.createEncryptionCryptoObject(keyName)
            Mode.DECRYPTION -> cryptoCreator.createDecryptionCryptoObject(keyName)
        }

        if (cryptoObject == null) {
            logger.log("CryptoObject not initialized.")
            callback.onError(Error.CRYPTO_OBJECT_INITIALIZATION)
            return
        }

        fingerprintCallback = CancellableAuthenticationCallback(value, mode, crypto, callback).also {
            fingerprintManagerCompat.authenticate(cryptoObject, 0, it.cancellationSignal, it, mainHandler)
        }
    }

    override fun cancel() {
        fingerprintCallback?.cancel()
    }
}