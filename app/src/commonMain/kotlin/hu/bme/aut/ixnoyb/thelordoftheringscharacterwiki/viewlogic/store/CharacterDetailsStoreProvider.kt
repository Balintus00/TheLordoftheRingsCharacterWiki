package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.CharacterRepository
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.Intent
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.Intent.Retry
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State.Loaded
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State.Loading
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State.LoadingFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStoreProvider.Action.LoadCharacter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStoreProvider.Message.FailedLoading
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStoreProvider.Message.StartedLoading
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStoreProvider.Message.SuccessfulLoading
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class CharacterDetailsStoreProvider(
    private val storeFactory: StoreFactory,
    private val characterId: Id,
) : KoinComponent {

    private val characterRepository: CharacterRepository by inject()

    fun create(): CharacterDetailsStore =
        object : CharacterDetailsStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = STORE_NAME,
            initialState = Loading(characterId),
            bootstrapper = SimpleBootstrapper<Action>(LoadCharacter),
            executorFactory = {
                Executor(
                    characterId = characterId,
                    repository = characterRepository,
                )
            },
            reducer = DefaultReducer,
        ) {}

    private sealed interface Action {

        data object LoadCharacter : Action
    }

    private class Executor(
        private val characterId: Id,
        private val repository: CharacterRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Nothing>() {

        private var characterCollectingJob: Job? = null

        override fun executeAction(action: Action) {
            if (action is LoadCharacter && state() is Loading) {
                startCollectingCharacter()
            }
        }

        private fun startCollectingCharacter() {
            characterCollectingJob?.cancel()

            characterCollectingJob = scope.launch {
                repository.getById(characterId).collect {
                    it?.let { dispatch(SuccessfulLoading(it)) } ?: dispatch(FailedLoading)
                }
            }
        }

        override fun executeIntent(intent: Intent) {
            when {
                intent is Retry && state() is LoadingFailed -> {
                    dispatch(StartedLoading)

                    characterCollectingJob?.cancel()

                    scope.launch {
                        try {
                            dispatch(SuccessfulLoading(repository.loadById(characterId)))
                        } catch (t: Throwable) {
                            dispatch(FailedLoading)
                        }
                    }
                }
            }
        }
    }

    private sealed interface Message {

        data class SuccessfulLoading(val character: Character) : Message

        data object FailedLoading : Message

        data object StartedLoading : Message
    }

    private object DefaultReducer : Reducer<State, Message> {

        override fun State.reduce(msg: Message): State = when (msg) {
            is SuccessfulLoading -> Loaded(character = msg.character)
            is FailedLoading -> LoadingFailed(characterId)
            is StartedLoading -> Loading(characterId)
        }
    }

    companion object {
        private const val STORE_NAME = "CharacterDetailStore"
    }
}