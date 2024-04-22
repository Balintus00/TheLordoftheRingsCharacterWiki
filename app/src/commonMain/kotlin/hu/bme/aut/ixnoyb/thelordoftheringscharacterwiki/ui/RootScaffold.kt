package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
internal fun RootScaffold(component: RootComponent, modifier: Modifier = Modifier) {
    val rootChild = component.childStack.collectAsState()

    val currentWindowWidthSizeClass = calculateWindowSizeClass().widthSizeClass

    var isBottomSheetDisplayed by remember { mutableStateOf(false) }
    var isDialogDisplayed by remember { mutableStateOf(false) }
    var previousWindowWidthSizeClass by remember { mutableStateOf(currentWindowWidthSizeClass) }
    val snackbarHostState = remember { SnackbarHostState() }
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (currentWindowWidthSizeClass != previousWindowWidthSizeClass) {
        previousWindowWidthSizeClass = currentWindowWidthSizeClass
        isBottomSheetDisplayed = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            Children(
                stack = rootChild.value,
                animation = stackAnimation(fade())
            ) { childContainer ->
                when (val child = childContainer.instance) {
                    is RootComponent.Child.CharacterFeature -> {
                        CharacterFeatureTopAppBar(
                            component = child.component,
                            modifier = Modifier.fillMaxWidth(),
                            showCompactListActionsAction = { isBottomSheetDisplayed = true },
                            showNameFilterAction = { isDialogDisplayed = true },
                            topAppBarScrollBehavior = topAppBarScrollBehavior,
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
                is RootComponent.Child.CharacterFeature -> {
                    CharacterFeatureScreen(
                        bottomSheetClosedAction = { isBottomSheetDisplayed = false },
                        component = child.component,
                        hideDialogAction = { isDialogDisplayed = false },
                        isBottomSheetDisplayed = isBottomSheetDisplayed,
                        isDialogDisplayed = isDialogDisplayed,
                        modifier = Modifier.fillMaxSize(),
                        snackbarHostState = snackbarHostState,
                        showDialogAction = { isDialogDisplayed = true },
                    )
                }

                is RootComponent.Child.Information -> {
                    InformationScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}