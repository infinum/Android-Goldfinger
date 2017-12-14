package co.infinum.goldfinger

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal

internal class CancellableAuthenticationCallback(
        private val value: String,
        private val mode: Mode,
        private val crypto: Crypto,
        private val callback: Goldfinger.Callback
) : FingerprintManagerCompat.AuthenticationCallback() {

    val cancellationSignal = CancellationSignal()

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        if (cancellationSignal.isCanceled.not()) {
            callback.onError(Error.fromId(errMsgId))
            cancellationSignal.cancel()
        }
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        if (cancellationSignal.isCanceled.not()) {
            val value = when (mode) {
                Mode.ENCRYPTION -> crypto.encrypt(result.cryptoObject, value)
                Mode.DECRYPTION -> crypto.decrypt(result.cryptoObject, value)
                Mode.AUTHENTICATE -> ""
            }

            if (value != null) {
                callback.onSuccess(value)
            } else {
                val error = if (mode == Mode.ENCRYPTION) Error.ENCRYPTION_FAILED else Error.DECRYPTION_FAILED
                callback.onError(error)
            }

            cancellationSignal.cancel()
        }
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        if (cancellationSignal.isCanceled.not()) {
            callback.onWarning(Warning.fromId(helpMsgId))
        }
    }

    override fun onAuthenticationFailed() {
        if (cancellationSignal.isCanceled.not()) {
            callback.onWarning(Warning.FAILURE)
        }
    }

    fun cancel() {
        if (cancellationSignal.isCanceled.not()) {
            cancellationSignal.cancel()
        }
    }
}