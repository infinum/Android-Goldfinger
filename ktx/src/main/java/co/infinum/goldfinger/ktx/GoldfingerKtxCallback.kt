package co.infinum.goldfinger.ktx

import co.infinum.goldfinger.Goldfinger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope

@ExperimentalCoroutinesApi
class GoldfingerKtxCallback(
    private val goldfinger: Goldfinger,
    private val producerScope: ProducerScope<Goldfinger.Result>,
) : Goldfinger.Callback {
    override fun onResult(result: Goldfinger.Result) {
        with(producerScope) {
            if (!isClosedForSend) {
                channel.trySend(result)
                if (result.type() == Goldfinger.Type.SUCCESS || result.type() == Goldfinger.Type.ERROR) {
                    channel.close()
                }
            } else {
                goldfinger.cancel()
            }
        }
    }

    override fun onError(e: Exception) {
        producerScope.channel.close(e)
    }
}