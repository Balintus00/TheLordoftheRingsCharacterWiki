package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.mvikotlin.core.store.StoreFactory
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.utility.toStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface RootComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed class Child {

        class CharacterFeature(val component: CharacterFeatureRootComponent) : Child()

        class Information(val component: InformationComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: StateFlow<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.CharacterFeature,
        handleBackButton = true,
        childFactory = ::createChild,
    ).toStateFlow()

    @OptIn(ExperimentalDecomposeApi::class)
    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child = when (config) {
        Config.CharacterFeature -> {
            RootComponent.Child.CharacterFeature(
                DefaultCharacterFeatureRootComponent(
                    componentContext = componentContext,
                    storeFactory = storeFactory,
                    navigateToInformationComponentAction = {
                        navigation.pushNew(Config.Information)
                    },
                )
            )
        }

        is Config.Information -> {
            RootComponent.Child.Information(
                DefaultInformationComponent(
                    componentContext = componentContext,
                    navigateBackAction = {
                        navigation.popWhile { config -> config !is Config.CharacterFeature }
                    },
                )
            )
        }
    }

    @Serializable
    private sealed class Config {

        @Serializable
        data object CharacterFeature : Config()

        @Serializable
        data object Information : Config()
    }
}