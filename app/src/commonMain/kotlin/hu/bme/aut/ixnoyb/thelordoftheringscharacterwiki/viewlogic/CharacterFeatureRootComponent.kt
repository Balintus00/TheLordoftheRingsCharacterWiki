package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.mvikotlin.core.store.StoreFactory
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterFeatureRootComponent.*
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterDetailsStoreProvider
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.store.CharacterListStoreProvider
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.createAndGetStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.getStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.removeStore
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.toStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface CharacterFeatureRootComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed class Child {
        class CharacterDetails(val component: CharacterDetailsComponent) : Child()

        class CharacterList(val component: CharacterListComponent) : Child()

        class CharacterListWithDetails(val component: CharacterListDetailComponent) : Child()
    }
}

internal class DefaultCharacterFeatureRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val navigateToInformationComponentAction: () -> Unit,
) : CharacterFeatureRootComponent, ComponentContext by componentContext {

    private val characterListStore = instanceKeeper.createAndGetStore {
        CharacterListStoreProvider(
            storeFactory = storeFactory,
        ).create()
    }

    private val characterDetailsStore: CharacterDetailsStore?
        get() = instanceKeeper.getStore<CharacterDetailsStore>()

    private val navigation = StackNavigation<Config>()

    override val childStack: StateFlow<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.CharacterList,
        handleBackButton = true,
        childFactory = ::createChild,
    ).toStateFlow()

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): Child {
        return when (config) {
            is Config.CharacterList -> {
                Child.CharacterList(
                    DefaultCharacterListComponent(
                        componentContext = componentContext,
                        store = characterListStore,
                        navigateToCharacterDetailsComponentAction = {
                            if (characterDetailsStore == null) {
                                navigation.push(Config.CharacterDetails(it.value))
                            }
                        },
                        navigateToCharacterListWithDetailsComponentAction = {
                            navigation.replaceAll(
                                Config.CharacterListWithDetails(characterId = null)
                            )
                        },
                        navigateToInformationComponentAction = navigateToInformationComponentAction,
                    )
                )
            }

            is Config.CharacterDetails -> {
                Child.CharacterDetails(
                    DefaultCharacterDetailsComponent(
                        componentContext = componentContext,
                        store = characterDetailsStore ?: createAndSetCharacterDetailsStore(
                            Id(config.characterId)
                        ),
                        navigateBackAction = {
                            navigation.popWhile { config -> config !is Config.CharacterList }

                            characterDetailsStore?.dispose()
                            instanceKeeper.remove(CharacterDetailsStore::class)
                        },
                        navigateToCharacterListWithDetailsComponentAction = {
                            navigation.replaceAll(
                                Config.CharacterListWithDetails(config.characterId)
                            )
                        },
                    )
                )
            }

            is Config.CharacterListWithDetails -> {
                Child.CharacterListWithDetails(
                    DefaultCharacterListDetailComponent(
                        componentContext = componentContext,
                        characterListStore = characterListStore,
                        clearCharacterDetailsStoreAction = {
                            instanceKeeper.removeStore<CharacterDetailsStore>()
                        },
                        createAndGetCharacterDetailsStoreAction = {
                            createAndSetCharacterDetailsStore(it)
                        },
                        getCharacterDetailsStoreAction = { characterDetailsStore },
                        navigateToCharacterListOrDetailsComponent = { detailedCharacterId ->
                            val modifiedNavigationStack = mutableListOf<Config>(
                                Config.CharacterList
                            )
                            detailedCharacterId?.let {
                                modifiedNavigationStack.add(
                                    Config.CharacterDetails(characterId = detailedCharacterId.value)
                                )
                            }
                            navigation.replaceAll(*modifiedNavigationStack.toTypedArray())
                        },
                        navigateToInformationComponentAction = navigateToInformationComponentAction,
                    )
                )
            }
        }
    }

    private fun createAndSetCharacterDetailsStore(characterId: Id): CharacterDetailsStore {
        characterDetailsStore?.dispose()
        instanceKeeper.removeStore<CharacterDetailsStore>()
        return instanceKeeper.createAndGetStore {
            CharacterDetailsStoreProvider(storeFactory, characterId).create()
        }
    }

    @Serializable
    private sealed class Config {

        @Serializable
        data class CharacterDetails(val characterId: String) : Config()

        @Serializable
        data object CharacterList : Config()

        @Serializable
        data class CharacterListWithDetails(val characterId: String?) : Config()
    }
}