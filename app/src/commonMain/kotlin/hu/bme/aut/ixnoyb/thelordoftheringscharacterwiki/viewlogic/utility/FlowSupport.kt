package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility

import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow

// Source: https://github.com/arkivanov/Decompose/issues/516#issuecomment-1791022312
fun <T : Any> Value<T>.asFlow(): Flow<T> = channelFlow {
    val cancellation = subscribe(channel::trySend)
    awaitClose(cancellation::cancel)
}

// Source: https://slack-chats.kotlinlang.org/t/16225841/how-can-i-convert-value-to-flow

fun <T : Any> Value<T>.toStateFlow(): StateFlow<T> = ValueStateFlow(this)

private class ValueStateFlow<out T : Any>(private val source: Value<T>) : StateFlow<T> {

    override val value: T
        get() = source.value

    override val replayCache: List<T>
        get() = listOf(source.value)

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        val flow = MutableStateFlow(source.value)
        val disposable = source.subscribe { flow.value = it }

        try {
            flow.collect(collector)
        } finally {
            disposable.cancel()
        }
    }
}