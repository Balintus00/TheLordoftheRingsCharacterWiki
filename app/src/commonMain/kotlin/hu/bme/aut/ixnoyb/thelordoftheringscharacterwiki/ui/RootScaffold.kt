package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.RootComponent

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RootScaffold(component: RootComponent, modifier: Modifier = Modifier) {
    val rootChild = component.childStack.collectAsState()

    var isBottomSheetDisplayed by remember { mutableStateOf(false) }

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            Children(
                stack = rootChild.value,
                animation = stackAnimation(fade())
            ) { childContainer ->
                when (val child = childContainer.instance) {
                    is RootComponent.Child.CharacterDetails -> {
                        CharacterDetailsTopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            navigateBackAction = { child.component.navigateBack() },
                            scrollBehavior = topAppBarScrollBehavior,
                        )
                    }

                    is RootComponent.Child.CharacterList, is RootComponent.Child.CharacterListWithDetails -> {
                        CharacterListTopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            showActionsAction = { isBottomSheetDisplayed = true },  // TODO menu
                        )
                    }

                    is RootComponent.Child.Information -> {
                        InformationScreenTopAppBar(
                            scrollBehavior = topAppBarScrollBehavior,
                            modifier = Modifier.fillMaxWidth(),
                            navigateBackAction = { child.component.navigateBack() },
                        )
                    }
                }
            }
        },
    ) {
        Children(
            stack = rootChild.value,
            modifier = Modifier.padding(it),
            animation = stackAnimation(slide())
        ) { childContainer ->
            when (val child = childContainer.instance) {
                is RootComponent.Child.CharacterDetails -> {
                    CharacterDetailsScreen(
                        component = child.component,
                    )
                }

                is RootComponent.Child.CharacterList -> {
                    CharacterListScreen(
                        component = child.component,
                        bottomSheetClosedAction = { isBottomSheetDisplayed = false },
                        isBottomSheetDisplayed = isBottomSheetDisplayed,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                is RootComponent.Child.CharacterListWithDetails -> {
                    CharacterListWithDetailsScreen(
                        component = child.component,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                is RootComponent.Child.Information -> {
                    InformationScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}