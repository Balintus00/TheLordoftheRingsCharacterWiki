package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store

import com.arkivanov.mvikotlin.core.store.Store
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageNumber
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.Intent
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStore.State

interface CharacterListStore : Store<Intent, State, Nothing> {

    sealed interface Intent {

        data class ApplyFilter(val nameFilter: CharacterNameFilter?) : Intent

        data object LoadNextPage : Intent

        data object Refresh : Intent

        data object Retry : Intent
    }

    sealed interface State {

        val activeNameFilter: CharacterNameFilter?

        data class FirstPageLoading(
            override val activeNameFilter: CharacterNameFilter? = null,
        ) : State

        data class FirstPageLoadingFailed(
            override val activeNameFilter: CharacterNameFilter?,
        ) : State

        sealed interface CharacterListContainerState : State {

            val characters: List<Character>

            val lastLoadedPageNumber: PageNumber

            fun getWithChangedCharacters(
                changedCharacters: List<Character>,
            ): CharacterListContainerState

            data class PageLoadedWithPossibleNextPages(
                override val activeNameFilter: CharacterNameFilter?,
                override val characters: List<Character>,
                override val lastLoadedPageNumber: PageNumber,
            ) : CharacterListContainerState {

                override fun getWithChangedCharacters(
                    changedCharacters: List<Character>,
                ): CharacterListContainerState = this.copy(characters = changedCharacters)
            }

            data class NextPageLoading(
                override val activeNameFilter: CharacterNameFilter?,
                override val characters: List<Character>,
                override val lastLoadedPageNumber: PageNumber,
            ) : CharacterListContainerState {

                override fun getWithChangedCharacters(
                    changedCharacters: List<Character>,
                ): CharacterListContainerState = this.copy(characters = changedCharacters)
            }

            data class NextPageLoadingFailed(
                override val activeNameFilter: CharacterNameFilter?,
                override val characters: List<Character>,
                override val lastLoadedPageNumber: PageNumber,
            ) : CharacterListContainerState {

                override fun getWithChangedCharacters(
                    changedCharacters: List<Character>,
                ): CharacterListContainerState = this.copy(characters = changedCharacters)
            }

            data class LastPageLoaded(
                override val activeNameFilter: CharacterNameFilter?,
                override val characters: List<Character>,
                override val lastLoadedPageNumber: PageNumber,
            ) : CharacterListContainerState {

                override fun getWithChangedCharacters(
                    changedCharacters: List<Character>,
                ): CharacterListContainerState = this.copy(characters = changedCharacters)
            }

            data class Refreshing(
                override val activeNameFilter: CharacterNameFilter?,
                override val characters: List<Character>,
                override val lastLoadedPageNumber: PageNumber,
                val isLastPageLoaded: Boolean,
            ) : CharacterListContainerState {

                override fun getWithChangedCharacters(
                    changedCharacters: List<Character>,
                ): CharacterListContainerState = this.copy(characters = changedCharacters)
            }

            data class RefreshingFailed(
                override val activeNameFilter: CharacterNameFilter?,
                override val characters: List<Character>,
                override val lastLoadedPageNumber: PageNumber,
                val isLastPageLoaded: Boolean,
            ) : CharacterListContainerState {

                override fun getWithChangedCharacters(
                    changedCharacters: List<Character>,
                ): CharacterListContainerState = this.copy(characters = changedCharacters)
            }
        }
    }
}