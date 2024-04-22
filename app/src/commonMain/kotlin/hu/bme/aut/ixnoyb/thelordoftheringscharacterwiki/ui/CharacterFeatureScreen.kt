package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterFeatureRootComponent

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CharacterFeatureTopAppBar(
    component: CharacterFeatureRootComponent,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    showCompactListActionsAction: () -> Unit = {},
    showNameFilterAction: () -> Unit = {},
) {
    val rootChild = component.childStack.collectAsState()

    Children(
        stack = rootChild.value,
        animation = stackAnimation(fade())
    ) { childContainer ->
        when (val child = childContainer.instance) {
            is CharacterFeatureRootComponent.Child.CharacterDetails -> {
                CharacterDetailsTopAppBar(
                    modifier = modifier,
                    navigateBackAction = child.component::navigateBack,
                    scrollBehavior = topAppBarScrollBehavior,
                )
            }

            is CharacterFeatureRootComponent.Child.CharacterList,
            is CharacterFeatureRootComponent.Child.CharacterListWithDetails
            -> {
                CharacterListTopAppBar(
                    modifier = modifier,
                    filterAction = showNameFilterAction,
                    informationAction = when (child) {
                        is CharacterFeatureRootComponent.Child.CharacterList -> {
                            { child.component.navigateToInformation() }
                        }

                        is CharacterFeatureRootComponent.Child.CharacterListWithDetails -> {
                            { child.component.navigateToInformation() }
                        }

                        else -> {
                            { /* No-op */ }
                        }
                    },
                    scrollBehavior = topAppBarScrollBehavior,
                    showCompactWidthActionsAction = showCompactListActionsAction,
                )
            }
        }
    }
}

@Composable
fun CharacterFeatureScreen(
    component: CharacterFeatureRootComponent,
    isBottomSheetDisplayed: Boolean,
    isDialogDisplayed: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    bottomSheetClosedAction: () -> Unit = {},
    hideDialogAction: () -> Unit = {},
    showDialogAction: () -> Unit = {},
) {
    val rootChild = component.childStack.collectAsState()

    Children(
        stack = rootChild.value,
        animation = stackAnimation(slide())
    ) { childContainer ->
        when (val child = childContainer.instance) {
            is CharacterFeatureRootComponent.Child.CharacterDetails -> {
                CharacterDetailsScreen(
                    component = child.component,
                    modifier = modifier,
                    snackbarHostState = snackbarHostState,
                )
            }

            is CharacterFeatureRootComponent.Child.CharacterList -> {
                CharacterListScreen(
                    bottomSheetClosedAction = bottomSheetClosedAction,
                    component = child.component,
                    hideNameFilterDialogAction = hideDialogAction,
                    isActionsBottomSheetDisplayed = isBottomSheetDisplayed,
                    isNameFilterDialogDisplayed = isDialogDisplayed,
                    modifier = modifier,
                    showNameFilterDialogAction = showDialogAction,
                    snackbarHostState = snackbarHostState,
                )
            }

            is CharacterFeatureRootComponent.Child.CharacterListWithDetails -> {
                CharacterListWithDetailsScreen(
                    bottomSheetClosedAction = bottomSheetClosedAction,
                    component = child.component,
                    hideNameFilterDialogAction = hideDialogAction,
                    isActionsBottomSheetDisplayed = isBottomSheetDisplayed,
                    isNameFilterDialogDisplayed = isDialogDisplayed,
                    modifier = modifier,
                    showNameFilterDialogAction = showDialogAction,
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}