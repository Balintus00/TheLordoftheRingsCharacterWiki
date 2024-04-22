package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store

import com.arkivanov.mvikotlin.core.store.Store
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.Intent
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State

interface CharacterDetailsStore : Store<Intent, State, Nothing> {

    sealed interface Intent {

        data object Retry : Intent
    }

    sealed interface State {

        val characterId: Id

        data class Loading(override val characterId: Id) : State

        data class Loaded(val character: Character) : State {

            override val characterId: Id
                get() = character.id
        }

        data class LoadingFailed(override val characterId: Id) : State
    }
}