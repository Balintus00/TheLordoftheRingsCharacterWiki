package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.mvikotlin.core.store.Store

fun <T : Store<*, *, *>> InstanceKeeper.createAndGetStore(key: Any, factory: () -> T): T =
    getOrCreate(key = key) {
        StoreInstance(factory())
    }.store

inline fun <reified T : Store<*, *, *>> InstanceKeeper.createAndGetStore(noinline factory: () -> T): T =
    createAndGetStore(key = T::class, factory = factory)

inline fun <reified T : Store<*, *, *>> InstanceKeeper.getStore(): T? =
    (get(T::class) as? StoreInstance<*>)?.store as? T

inline fun <reified T : Store<*, *, *>> InstanceKeeper.removeStore() =
    remove(T::class)

class StoreInstance<out T : Store<*, *, *>>(
    val store: T
) : InstanceKeeper.Instance {
    override fun onDestroy() {
        store.dispose()
    }
}