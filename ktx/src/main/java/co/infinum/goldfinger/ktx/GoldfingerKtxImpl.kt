package co.infinum.goldfinger.ktx

import co.infinum.goldfinger.Goldfinger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable

@ExperimentalCoroutinesApi
class GoldfingerKtxImpl(private val goldfinger: Goldfinger) : GoldfingerKtx {

    private fun inGoldfingerFlow(doWithCallback: (GoldfingerKtxCallback) -> Unit) =
        callbackFlow {
            doWithCallback(
                GoldfingerKtxCallback(
                    goldfinger,
                    this,
                )
            )
            awaitClose { goldfinger.cancel() }
        }
            .buffer(
                capacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )
            .cancellable()

    override fun hasFingerprintHardware(): Boolean = goldfinger.hasFingerprintHardware()

    override fun hasEnrolledFingerprint(): Boolean = goldfinger.hasEnrolledFingerprint()

    override fun canAuthenticate(): Boolean = goldfinger.canAuthenticate()

    override fun authenticate(params: Goldfinger.PromptParams): Flow<Goldfinger.Result> = inGoldfingerFlow { callback ->
        goldfinger.authenticate(
            params,
            callback,
        )
    }

    override fun encrypt(
        params: Goldfinger.PromptParams,
        key: String,
        value: String,
    ): Flow<Goldfinger.Result> = inGoldfingerFlow { callback ->
        goldfinger.encrypt(
            params,
            key,
            value,
            callback,
        )
    }

    override fun decrypt(
        params: Goldfinger.PromptParams,
        key: String,
        value: String,
    ): Flow<Goldfinger.Result> = inGoldfingerFlow { callback ->
        goldfinger.decrypt(
            params,
            key,
            value,
            callback,
        )
    }

    override fun cancel() {
        goldfinger.cancel()
    }
}
