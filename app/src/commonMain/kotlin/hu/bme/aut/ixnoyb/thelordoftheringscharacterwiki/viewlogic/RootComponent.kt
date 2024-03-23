package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.toStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface RootComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed class Child {
        class CharacterDetails(val component: CharacterDetailsComponent) : Child()

        class CharacterList(val component: CharacterListComponent) : Child()

        class CharacterListWithDetails(val component: CharacterListWithDetailsComponent) : Child()
        class Information(val component: InformationComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: StateFlow<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.CharacterList,
        handleBackButton = true,
        childFactory = ::createChild,
    ).toStateFlow()

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child {
        val navigateBackAction = { navigation.pop() }
        val navigateToInformationComponentAction = { navigation.push(Config.Information) }

        return when (config) {
            is Config.CharacterDetails -> {
                RootComponent.Child.CharacterDetails(
                    DefaultCharacterDetailsComponent(
                        componentContext = componentContext,
                        characterId = config.characterId,
                        navigateBackAction = navigateBackAction,
                        navigateToCharacterListWithDetailsComponentAction = {
                            navigation.replaceAll(
                                Config.CharacterListWithDetails(config.characterId)
                            )
                        },
                    )
                )
            }

            is Config.CharacterList -> {
                RootComponent.Child.CharacterList(
                    DefaultCharacterListComponent(
                        componentContext = componentContext,
                        navigateToCharacterDetailsComponentAction = {
                            navigation.push(Config.CharacterDetails(it))
                        },
                        navigateToCharacterListWithDetailsComponentAction = {
                            navigation.replaceAll(
                                Config.CharacterListWithDetails(
                                    characterId = null,
                                )
                            )
                        },
                        navigateToInformationComponentAction = navigateToInformationComponentAction,
                    )
                )
            }

            is Config.CharacterListWithDetails -> {
                RootComponent.Child.CharacterListWithDetails(
                    DefaultCharacterListWithDetailsComponent(
                        componentContext = componentContext,
                        selectedCharacterId = config.characterId,
                        navigateToCharacterListOrDetailsComponent = { selectedCharacterId ->
                            val modifiedNavigationStack = mutableListOf<Config>(
                                Config.CharacterList
                            )
                            selectedCharacterId?.let {
                                modifiedNavigationStack.add(
                                    Config.CharacterDetails(characterId = it)
                                )
                            }
                            navigation.replaceAll(*modifiedNavigationStack.toTypedArray())
                        },
                        navigateToInformationComponentAction = navigateToInformationComponentAction,
                    )
                )
            }

            is Config.Information -> {
                RootComponent.Child.Information(
                    DefaultInformationComponent(
                        componentContext = componentContext,
                        navigateBackAction = navigateBackAction,
                    )
                )
            }
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

        @Serializable
        data object Information : Config()
    }
}