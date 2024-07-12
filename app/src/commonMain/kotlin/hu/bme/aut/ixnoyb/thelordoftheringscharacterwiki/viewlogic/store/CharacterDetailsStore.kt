package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store

import com.arkivanov.mvikotlin.core.store.Store
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.ID
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.Intent
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore.State

internal interface CharacterDetailsStore : Store<Intent, State, Nothing> {

    sealed interface Intent {

        data object Retry : Intent
    }

    sealed interface State {

        val characterID: ID

        data class Loading(override val characterID: ID) : State

        data class Loaded(val character: Character) : State {

            override val characterID: ID
                get() = character.id
        }

        data class LoadingFailed(override val characterID: ID) : State
    }
}