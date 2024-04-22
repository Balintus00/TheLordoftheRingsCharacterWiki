package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getViewStateStateFlow
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterDetailsComponent.ViewState
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model.toUiModel
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State.Loaded
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State.Loading
import kotlinx.coroutines.flow.StateFlow

interface CharacterDetailsComponent {

    val viewState: StateFlow<ViewState>

    fun navigateBack()

    fun navigateToCharacterListWithDetails()

    interface ViewState {

        interface Loading : ViewState

        interface Loaded : ViewState {

            val character: Character
        }

        interface LoadingFailed : ViewState {

            fun retryLoading()
        }
    }
}

class DefaultCharacterDetailsComponent(
    componentContext: ComponentContext,
    private val store: CharacterDetailsStore,
    private val navigateBackAction: () -> Unit,
    private val navigateToCharacterListWithDetailsComponentAction: () -> Unit,
) : CharacterDetailsComponent, ComponentContext by componentContext {

    override val viewState: StateFlow<ViewState> = store.getViewStateStateFlow(
        component = this,
        mapper = { it.toViewState(store) },
    )

    override fun navigateBack() {
        navigateBackAction()
    }

    override fun navigateToCharacterListWithDetails() {
        navigateToCharacterListWithDetailsComponentAction()
    }
}

fun State.toViewState(store: CharacterDetailsStore): ViewState = when (this) {
    is Loading -> object : ViewState.Loading {}

    is Loaded -> object : ViewState.Loaded {

        override val character: Character = this@toViewState.character.toUiModel()
    }

    is State.LoadingFailed -> object : ViewState.LoadingFailed {

        override fun retryLoading() {
            store.accept(CharacterDetailsStore.Intent.Retry)
        }
    }
}