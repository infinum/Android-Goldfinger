package co.infinum.goldfinger.ktx

import android.content.Context
import co.infinum.goldfinger.Goldfinger
import co.infinum.goldfinger.Goldfinger.PromptParams
import co.infinum.goldfinger.crypto.CipherCrypter
import co.infinum.goldfinger.crypto.CipherFactory
import co.infinum.goldfinger.crypto.MacCrypter
import co.infinum.goldfinger.crypto.MacFactory
import co.infinum.goldfinger.crypto.SignatureCrypter
import co.infinum.goldfinger.crypto.SignatureFactory
import kotlinx.coroutines.flow.Flow

interface GoldfingerKtx {

    fun hasFingerprintHardware(): Boolean

    fun hasEnrolledFingerprint(): Boolean

    fun canAuthenticate(): Boolean

    suspend fun authenticate(
        params: PromptParams,
    ): Flow<Goldfinger.Result>

    suspend fun encrypt(
        params: PromptParams,
        key: String,
        value: String,
    ): Flow<Goldfinger.Result>

    suspend fun decrypt(
        params: PromptParams,
        key: String,
        value: String,
    ): Flow<Goldfinger.Result>

    fun cancel()

    class Builder(private val context: Context) {

        private val baseBuilder: Goldfinger.Builder = Goldfinger.Builder(context)

        fun build(): GoldfingerKtx =
            GoldfingerKtxImpl(baseBuilder.build())

        fun cipherCrypter(cipherCrypter: CipherCrypter?): Builder = apply {
            baseBuilder.cipherCrypter(cipherCrypter)
        }

        fun cipherFactory(cipherFactory: CipherFactory?): Builder = apply {
            baseBuilder.cipherFactory(cipherFactory)
        }

        fun logEnabled(logEnabled: Boolean): Builder = apply {
            baseBuilder.logEnabled(logEnabled)
        }

        fun macCrypter(macCrypter: MacCrypter?): Builder = apply {
            baseBuilder.macCrypter(macCrypter)
        }

        fun macFactory(macFactory: MacFactory?): Builder = apply {
            baseBuilder.macFactory(macFactory)
        }

        fun signatureCrypter(signatureCrypter: SignatureCrypter?): Builder = apply {
            baseBuilder.signatureCrypter(signatureCrypter)
        }

        fun signatureFactory(signatureFactory: SignatureFactory?): Builder = apply {
            baseBuilder.signatureFactory(signatureFactory)
        }
    }
}
