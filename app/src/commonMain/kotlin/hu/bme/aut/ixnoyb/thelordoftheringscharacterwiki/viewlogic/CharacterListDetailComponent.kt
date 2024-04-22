package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getViewStateStateFlow
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListDetailComponent.ViewState
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.componentScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CharacterListDetailComponent {

    val viewState: StateFlow<ViewState>

    fun navigateToCharacterListOrDetails()

    fun navigateToInformation()

    interface ViewState {

        val characterListViewState: CharacterListComponent.ViewState

        val characterDetailsViewState: CharacterDetailsComponent.ViewState?

        fun selectCharacter(characterId: String)
    }
}

internal class DefaultCharacterListDetailComponent(
    componentContext: ComponentContext,
    private val characterListStore: CharacterListStore,
    private val clearCharacterDetailsStoreAction: () -> Unit,
    private val createAndGetCharacterDetailsStoreAction: (characterId: Id) -> CharacterDetailsStore,
    private val getCharacterDetailsStoreAction: () -> CharacterDetailsStore?,
    private val navigateToCharacterListOrDetailsComponent: (Id?) -> Unit,
    private val navigateToInformationComponentAction: () -> Unit,
) : CharacterListDetailComponent, ComponentContext by componentContext {

    private val log = Logger.withTag(DefaultCharacterListDetailComponent::class.simpleName!!)

    private var characterDetailsStateCollectionJob: Job? = null

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(
        object : ViewState {

            override val characterListViewState = characterListStore.state.toViewState(
                store = characterListStore,
                onFilterTooLongAction =
                this@DefaultCharacterListDetailComponent::logTooLongFilterError,
            )

            override val characterDetailsViewState = getCharacterDetailsStoreAction()?.let {
                it.state.toViewState(it)
            }

            override fun selectCharacter(characterId: String) {
                getAndUpdateSelectedCharacterDetailsStore(Id(characterId))
            }
        }
    )

    private fun logTooLongFilterError(filter: String) {
        log.w { "Too long character name filter was tried to be used: $filter" }
    }

    private fun getAndUpdateSelectedCharacterDetailsStore(characterId: Id) {
        collectCharacterDetailsState(createAndGetCharacterDetailsStoreAction(characterId))
    }

    override val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    init {
        collectCharacterListState()
        getCharacterDetailsStoreAction()?.let { collectCharacterDetailsState(it) }
    }

    private fun collectCharacterListState() {
        componentScope.launch {
            characterListStore.getViewStateStateFlow(
                component = this@DefaultCharacterListDetailComponent,
                mapper = {
                    it.toViewState(
                        store = characterListStore,
                        onFilterTooLongAction =
                        this@DefaultCharacterListDetailComponent::logTooLongFilterError,
                    )
                },
            ).collect { updatedCharacterListViewState ->
                val isSelectedCharacterToBeCleared =
                    updatedCharacterListViewState is CharacterListComponent.ViewState.FirstPageLoading

                if (isSelectedCharacterToBeCleared) {
                    clearCharacterDetailsStoreAction()
                    characterDetailsStateCollectionJob?.cancel()
                    characterDetailsStateCollectionJob = null
                }

                _viewState.update { previousViewState ->
                    object : ViewState {

                        override val characterListViewState = updatedCharacterListViewState

                        override val characterDetailsViewState =
                            if (isSelectedCharacterToBeCleared) {
                                null
                            } else {
                                previousViewState.characterDetailsViewState
                            }

                        override fun selectCharacter(characterId: String) {
                            getAndUpdateSelectedCharacterDetailsStore(Id(characterId))
                        }
                    }
                }
            }
        }
    }

    private fun collectCharacterDetailsState(characterDetailsStore: CharacterDetailsStore) {
        characterDetailsStateCollectionJob?.cancel()

        characterDetailsStateCollectionJob = componentScope.launch {
            characterDetailsStore.getViewStateStateFlow(
                component = this@DefaultCharacterListDetailComponent,
                mapper = { it.toViewState(characterDetailsStore) },
            ).collect { updatedCharacterDetailsViewState ->
                _viewState.update { previousViewState ->
                    object : ViewState {

                        override val characterListViewState =
                            previousViewState.characterListViewState

                        override val characterDetailsViewState = updatedCharacterDetailsViewState

                        override fun selectCharacter(characterId: String) {
                            getAndUpdateSelectedCharacterDetailsStore(Id(characterId))
                        }
                    }
                }
            }
        }
    }

    override fun navigateToCharacterListOrDetails() {
        navigateToCharacterListOrDetailsComponent(
            getCharacterDetailsStoreAction()?.state?.characterId,
        )
    }

    override fun navigateToInformation() {
        navigateToInformationComponentAction()
    }
}