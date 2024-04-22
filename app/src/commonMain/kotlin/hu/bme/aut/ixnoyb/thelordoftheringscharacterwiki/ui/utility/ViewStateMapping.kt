package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.states
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

inline fun <Intent : Any, State : Any, Label : Any, ViewState : Any>
        Store<Intent, State, Label>.getViewStateStateFlow(
    component: ComponentContext,
    crossinline mapper: (State) -> ViewState,
): StateFlow<ViewState> = this.states.map { mapper(it) }.stateIn(
    component.componentScope,
    SharingStarted.WhileSubscribed(),
    mapper(state),
)