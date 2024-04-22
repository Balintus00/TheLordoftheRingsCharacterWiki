package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageNumber
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageNumber.Companion.FIRST_NUMBER
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSize
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.CharacterRepository
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.Intent
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.Intent.ApplyFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.Intent.LoadNextPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.Intent.Refresh
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.Intent.Retry
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.CharacterListContainerState
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.CharacterListContainerState.LastPageLoaded
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.CharacterListContainerState.NextPageLoading
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.CharacterListContainerState.NextPageLoadingFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.CharacterListContainerState.PageLoadedWithPossibleNextPages
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.CharacterListContainerState.Refreshing
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.CharacterListContainerState.RefreshingFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.FirstPageLoading
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State.FirstPageLoadingFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider.Action.LoadFirstPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider.Message.CharactersChanged
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider.Message.FinishPageLoadingWithFailure
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider.Message.FinishedPageLoadingWithSuccess
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider.Message.StartedLoadingNextPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider.Message.StartedLoadingWithNewFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider.Message.StartedRefreshing
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CharacterListStoreProvider(
    private val storeFactory: StoreFactory,
) : KoinComponent {

    private val characterRepository: CharacterRepository by inject()

    fun create(): CharacterListStore =
        object : CharacterListStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = STORE_NAME,
            initialState = FirstPageLoading(activeNameFilter = null),
            bootstrapper = SimpleBootstrapper<Action>(LoadFirstPage),
            executorFactory = { Executor(repository = characterRepository) },
            reducer = DefaultReducer,
        ) {}

    private sealed interface Action {

        data object LoadFirstPage : Action
    }

    private class Executor(private val repository: CharacterRepository) :
        CoroutineExecutor<Intent, Action, State, Message, Nothing>() {

        private var characterCollectorJob: Job? = null

        private var firstPageLoadingJob: Job? = null
        private var nextPageLoadingJob: Job? = null
        private var refreshingJob: Job? = null

        override fun executeAction(action: Action) {
            val currentState = state()
            if (currentState is FirstPageLoading) {
                val nameFilter = currentState.activeNameFilter

                firstPageLoadingJob = scope.launch {
                    loadPage(
                        nameFilter = nameFilter,
                        numberOfPageToLoad = FIRST_NUMBER,
                        onSuccessfulLoadingAction = { collectCharacters(nameFilter) },
                    )
                }
            }
        }

        private suspend fun loadPage(
            numberOfPageToLoad: PageNumber,
            nameFilter: CharacterNameFilter? = null,
            onSuccessfulLoadingAction: () -> Unit = {},
            onFailedLoadingAction: () -> Unit = {},
        ) {
            try {
                val charactersWithNextPageExistence = repository.loadPage(
                    nameFilter = nameFilter, page = PageSpecification(
                        number = numberOfPageToLoad,
                        size = PageSize(CHARACTER_LIST_PAGE_SIZE),
                    )
                )

                onSuccessfulLoadingAction()

                dispatch(
                    FinishedPageLoadingWithSuccess(
                        loadedCharacterList = charactersWithNextPageExistence.first,
                        isNextPageExist = charactersWithNextPageExistence.second,
                    )
                )
            } catch (t: Throwable) {
                onFailedLoadingAction()

                dispatch(FinishPageLoadingWithFailure)
            }
        }

        private fun collectCharacters(nameFilter: CharacterNameFilter?) {
            characterCollectorJob?.cancel()

            characterCollectorJob = scope.launch {
                repository.getAll(nameFilter).collect {
                    dispatch(CharactersChanged(it))
                }
            }
        }

        override fun executeIntent(intent: Intent) {
            val state = state()

            when {
                intent is ApplyFilter -> {
                    val nameFilter = intent.nameFilter

                    dispatch(StartedLoadingWithNewFilter(nameFilter))

                    firstPageLoadingJob?.cancel()
                    refreshingJob?.cancel()
                    nextPageLoadingJob?.cancel()

                    firstPageLoadingJob = scope.launch {
                        loadPage(
                            nameFilter = nameFilter,
                            numberOfPageToLoad = FIRST_NUMBER,
                            onSuccessfulLoadingAction = { collectCharacters(nameFilter) },
                        )
                    }
                }

                intent is LoadNextPage && state is CharacterListContainerState && (
                        state is PageLoadedWithPossibleNextPages ||
                                state is NextPageLoadingFailed ||
                                (state is RefreshingFailed && state.isLastPageLoaded.not())
                        )
                -> {
                    dispatch(StartedLoadingNextPage)

                    nextPageLoadingJob = scope.launch {
                        loadPage(
                            nameFilter = state.activeNameFilter,
                            numberOfPageToLoad = state.lastLoadedPageNumber.getNextNumber(),
                        )
                    }
                }

                intent is Refresh && (
                        state is PageLoadedWithPossibleNextPages ||
                                state is NextPageLoading ||
                                state is NextPageLoadingFailed ||
                                state is LastPageLoaded ||
                                state is RefreshingFailed
                        )
                -> {
                    val nameFilter = state.activeNameFilter
                    val characterCollectionAction = { collectCharacters(nameFilter) }

                    dispatch(StartedRefreshing)

                    nextPageLoadingJob?.cancel()
                    characterCollectorJob?.cancel()

                    refreshingJob = scope.launch {
                        loadPage(
                            nameFilter = nameFilter,
                            numberOfPageToLoad = FIRST_NUMBER,
                            onSuccessfulLoadingAction = characterCollectionAction,
                            onFailedLoadingAction = characterCollectionAction,
                        )
                    }
                }

                intent is Retry && state is FirstPageLoadingFailed -> {
                    val nameFilter = state.activeNameFilter

                    dispatch(StartedLoadingWithNewFilter(nameFilter))

                    firstPageLoadingJob = scope.launch {
                        loadPage(
                            nameFilter = nameFilter,
                            numberOfPageToLoad = FIRST_NUMBER,
                            onSuccessfulLoadingAction = { collectCharacters(nameFilter) },
                        )
                    }
                }

                intent is Retry && state is NextPageLoadingFailed -> {
                    dispatch(StartedLoadingNextPage)

                    nextPageLoadingJob = scope.launch {
                        loadPage(
                            nameFilter = state.activeNameFilter,
                            numberOfPageToLoad = state.lastLoadedPageNumber.getNextNumber(),
                        )
                    }
                }

                intent is Retry && state is RefreshingFailed -> {
                    dispatch(StartedRefreshing)

                    refreshingJob = scope.launch {
                        loadPage(
                            nameFilter = state.activeNameFilter,
                            numberOfPageToLoad = FIRST_NUMBER,
                        )
                    }
                }
            }
        }

        companion object {
            private const val CHARACTER_LIST_PAGE_SIZE = 20
        }
    }

    private sealed class Message {

        data class StartedLoadingWithNewFilter(val newFilter: CharacterNameFilter?) : Message()

        data object StartedLoadingNextPage : Message()

        data object StartedRefreshing : Message()

        data class FinishedPageLoadingWithSuccess(
            val loadedCharacterList: List<Character>,
            val isNextPageExist: Boolean,
        ) : Message()

        data object FinishPageLoadingWithFailure : Message()

        data class CharactersChanged(val characters: List<Character>) : Message()
    }

    private object DefaultReducer : Reducer<State, Message> {

        override fun State.reduce(msg: Message): State {
            return when {
                msg is StartedLoadingWithNewFilter -> {
                    FirstPageLoading(activeNameFilter = msg.newFilter)
                }

                msg is StartedLoadingNextPage && this is CharacterListContainerState -> {
                    NextPageLoading(
                        activeNameFilter = activeNameFilter,
                        characters = characters,
                        lastLoadedPageNumber = lastLoadedPageNumber,
                    )
                }

                msg is StartedRefreshing && this is CharacterListContainerState && (
                        this is LastPageLoaded ||
                                (this is RefreshingFailed && this.isLastPageLoaded)
                        )
                -> {
                    Refreshing(
                        activeNameFilter = activeNameFilter,
                        characters = characters,
                        lastLoadedPageNumber = lastLoadedPageNumber,
                        isLastPageLoaded = true,
                    )
                }

                msg is StartedRefreshing && this is CharacterListContainerState && (
                        this is PageLoadedWithPossibleNextPages ||
                                this is NextPageLoading ||
                                this is NextPageLoadingFailed ||
                                (this is RefreshingFailed && this.isLastPageLoaded.not())
                        )
                -> {
                    Refreshing(
                        activeNameFilter = activeNameFilter,
                        characters = characters,
                        lastLoadedPageNumber = lastLoadedPageNumber,
                        isLastPageLoaded = false,
                    )
                }

                msg is FinishedPageLoadingWithSuccess && msg.isNextPageExist
                        && (this is FirstPageLoading || this is Refreshing)
                -> {
                    PageLoadedWithPossibleNextPages(
                        activeNameFilter = activeNameFilter,
                        characters = msg.loadedCharacterList,
                        lastLoadedPageNumber = FIRST_NUMBER,
                    )
                }

                msg is FinishedPageLoadingWithSuccess &&
                        (this is FirstPageLoading || this is Refreshing)
                -> {
                    LastPageLoaded(
                        activeNameFilter = activeNameFilter,
                        characters = msg.loadedCharacterList,
                        lastLoadedPageNumber = FIRST_NUMBER,
                    )
                }

                msg is FinishedPageLoadingWithSuccess && msg.isNextPageExist
                        && this is NextPageLoading
                -> {
                    PageLoadedWithPossibleNextPages(
                        activeNameFilter = activeNameFilter,
                        characters = characters,
                        lastLoadedPageNumber = lastLoadedPageNumber.getNextNumber(),
                    )
                }

                msg is FinishedPageLoadingWithSuccess && this is NextPageLoading -> {
                    LastPageLoaded(
                        activeNameFilter = activeNameFilter,
                        characters = characters,
                        lastLoadedPageNumber = lastLoadedPageNumber.getNextNumber(),
                    )
                }

                msg is FinishPageLoadingWithFailure && this is FirstPageLoading -> {
                    FirstPageLoadingFailed(activeNameFilter)
                }

                msg is FinishPageLoadingWithFailure && this is Refreshing -> {
                    RefreshingFailed(
                        activeNameFilter = activeNameFilter,
                        characters = characters,
                        lastLoadedPageNumber = lastLoadedPageNumber,
                        isLastPageLoaded = isLastPageLoaded,
                    )
                }

                msg is FinishPageLoadingWithFailure && this is NextPageLoading -> {
                    NextPageLoadingFailed(
                        activeNameFilter = activeNameFilter,
                        characters = characters,
                        lastLoadedPageNumber = lastLoadedPageNumber,
                    )
                }

                msg is CharactersChanged && this is CharacterListContainerState -> {
                    getWithChangedCharacters(msg.characters)
                }

                else -> this
            }
        }
    }

    companion object {
        private const val STORE_NAME = "CharacterListStore"
    }
}