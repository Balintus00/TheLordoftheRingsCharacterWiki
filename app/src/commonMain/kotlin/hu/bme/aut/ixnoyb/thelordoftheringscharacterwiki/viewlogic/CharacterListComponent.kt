package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getViewStateStateFlow
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState.CharactersAvailable
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState.FirstPageLoading
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState.OperationFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model.CharacterListItem
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model.toListItem
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.Intent.Retry
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State
import kotlinx.coroutines.flow.StateFlow

interface CharacterListComponent {

    val viewState: StateFlow<ViewState>

    fun navigateToCharacterDetails(characterId: String)

    fun navigateToCharacterListWithDetails()

    fun navigateToInformation()

    interface ViewState {

        val currentNameFilter: String?

        fun applyFilter(filter: String?)

        interface FirstPageLoading : ViewState

        interface OperationFailed : ViewState {

            fun retryOperation()
        }

        interface CharactersAvailable : ViewState {

            val characters: List<CharacterListItem>

            val isNextPageLoading: Boolean

            val isRefreshing: Boolean

            fun loadNextPage()

            fun refresh()
        }
    }

    companion object {
        const val CHARACTER_NAME_FILTER_MINIMUM_LENGTH = CharacterNameFilter.MINIMUM_LENGTH
        const val CHARACTER_NAME_FILTER_MAXIMUM_LENGTH = CharacterNameFilter.MAXIMUM_LENGTH
    }
}

internal class DefaultCharacterListComponent(
    componentContext: ComponentContext,
    val store: CharacterListStore,
    private val navigateToCharacterDetailsComponentAction: (Id) -> Unit,
    private val navigateToCharacterListWithDetailsComponentAction: () -> Unit,
    private val navigateToInformationComponentAction: () -> Unit,
) : CharacterListComponent, ComponentContext by componentContext {

    private val log = Logger.withTag(DefaultCharacterListComponent::class.simpleName!!)

    override val viewState: StateFlow<ViewState> = store.getViewStateStateFlow(
        component = this,
        mapper = {
            it.toViewState(
                store = store,
                onFilterTooLongAction = {
                    log.w { "Too long character name filter was tried to be used: $it" }
                },
            )
        },
    )

    override fun navigateToCharacterDetails(characterId: String) {
        navigateToCharacterDetailsComponentAction(Id(characterId))
    }

    override fun navigateToCharacterListWithDetails() {
        navigateToCharacterListWithDetailsComponentAction()
    }

    override fun navigateToInformation() {
        navigateToInformationComponentAction()
    }
}

internal fun State.toViewState(
    store: CharacterListStore,
    onFilterTooLongAction: (String) -> Unit,
): ViewState {
    return when (this) {
        is State.FirstPageLoading -> createFirstPageLoadingState(
            store = store,
            onFilterTooLongAction = onFilterTooLongAction,
        )

        is State.FirstPageLoadingFailed -> createOperationFailedState(
            store = store,
            onFilterTooLongAction = onFilterTooLongAction,
        )

        is State.CharacterListContainerState.NextPageLoading -> createCharactersAvailableState(
            store = store,
            isLoadingNextPage = true,
            isRefreshing = false,
            onFilterTooLongAction = onFilterTooLongAction,
        )

        is State.CharacterListContainerState.NextPageLoadingFailed
        -> createCharactersAvailableWithOperationFailureState(
            store = store,
            onFilterTooLongAction = onFilterTooLongAction,
        )

        is State.CharacterListContainerState.Refreshing -> createCharactersAvailableState(
            store = store,
            isLoadingNextPage = false,
            isRefreshing = true,
            onFilterTooLongAction = onFilterTooLongAction,
        )

        is State.CharacterListContainerState.RefreshingFailed
        -> createCharactersAvailableWithOperationFailureState(
            store = store,
            onFilterTooLongAction = onFilterTooLongAction,
        )

        is State.CharacterListContainerState -> createCharactersAvailableState(
            store = store,
            isLoadingNextPage = false,
            isRefreshing = false,
            onFilterTooLongAction = onFilterTooLongAction,
        )
    }
}

private fun State.createFirstPageLoadingState(
    store: CharacterListStore,
    onFilterTooLongAction: (String) -> Unit,
) = object : FirstPageLoading {

    private val baseState = createBaseViewState(
        store = store,
        onFilterTooLongAction = onFilterTooLongAction,
    )

    override val currentNameFilter: String? = baseState.currentNameFilter

    override fun applyFilter(filter: String?) {
        baseState.applyFilter(filter)
    }
}

private fun State.createBaseViewState(
    store: CharacterListStore,
    onFilterTooLongAction: (String) -> Unit,
) = object : ViewState {

    override val currentNameFilter: String? = this@createBaseViewState.activeNameFilter?.value

    override fun applyFilter(filter: String?) {
        sendApplyFilterIntent(
            store = store,
            filter = filter,
            onFilterTooLongAction = onFilterTooLongAction,
        )
    }
}

private fun sendApplyFilterIntent(
    store: CharacterListStore,
    filter: String?,
    onFilterTooLongAction: (String) -> Unit,
) {
    when {
        filter == null ||
                filter.length < CharacterListComponent.CHARACTER_NAME_FILTER_MINIMUM_LENGTH
        -> {
            store.accept(CharacterListStore.Intent.ApplyFilter(null))
        }

        filter.length <= CharacterListComponent.CHARACTER_NAME_FILTER_MAXIMUM_LENGTH -> {
            store.accept(CharacterListStore.Intent.ApplyFilter(CharacterNameFilter(filter)))
        }

        else -> {
            onFilterTooLongAction(filter)
        }
    }
}

private fun State.createOperationFailedState(
    store: CharacterListStore,
    onFilterTooLongAction: (String) -> Unit,
) = object : OperationFailed {

    private val baseState = createBaseViewState(
        store = store,
        onFilterTooLongAction = onFilterTooLongAction,
    )

    override val currentNameFilter: String? = baseState.currentNameFilter

    override fun applyFilter(filter: String?) {
        baseState.applyFilter(filter)
    }

    override fun retryOperation() {
        sendRetry(store)
    }
}

private fun sendRetry(store: CharacterListStore) {
    store.accept(Retry)
}

private fun State.CharacterListContainerState.createCharactersAvailableState(
    store: CharacterListStore,
    isLoadingNextPage: Boolean,
    isRefreshing: Boolean,
    onFilterTooLongAction: (String) -> Unit,
) = object : CharactersAvailable {

    private val baseState = createBaseViewState(
        store = store,
        onFilterTooLongAction = onFilterTooLongAction,
    )

    override val currentNameFilter: String? = baseState.currentNameFilter

    override val characters: List<CharacterListItem>
        get() = this@createCharactersAvailableState.characters.map { it.toListItem() }

    override val isNextPageLoading: Boolean = isLoadingNextPage

    override val isRefreshing: Boolean = isRefreshing

    override fun applyFilter(filter: String?) {
        baseState.applyFilter(filter)
    }

    override fun loadNextPage() {
        sendLoadNextPage(store)
    }

    override fun refresh() {
        sendRefresh(store)
    }
}

private fun sendLoadNextPage(store: CharacterListStore) {
    store.accept(CharacterListStore.Intent.LoadNextPage)
}

private fun sendRefresh(store: CharacterListStore) {
    store.accept(CharacterListStore.Intent.Refresh)
}

private fun State.CharacterListContainerState.createCharactersAvailableWithOperationFailureState(
    store: CharacterListStore,
    onFilterTooLongAction: (String) -> Unit,
) = object : CharactersAvailable, OperationFailed {

    private val charactersAvailableState = createCharactersAvailableState(
        store = store,
        isLoadingNextPage = false,
        isRefreshing = false,
        onFilterTooLongAction = onFilterTooLongAction,
    )

    override val currentNameFilter: String? = charactersAvailableState.currentNameFilter

    override val characters: List<CharacterListItem> = charactersAvailableState.characters

    override val isNextPageLoading: Boolean = charactersAvailableState.isNextPageLoading

    override val isRefreshing: Boolean = charactersAvailableState.isRefreshing

    override fun applyFilter(filter: String?) {
        charactersAvailableState.applyFilter(filter)
    }

    override fun loadNextPage() {
        charactersAvailableState.loadNextPage()
    }

    override fun refresh() {
        charactersAvailableState.refresh()
    }

    override fun retryOperation() {
        sendRetry(store)
    }
}