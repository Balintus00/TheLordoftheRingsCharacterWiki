package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme.Material3Typography
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.collectAsStateWithLifecycle
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getStandardSpace
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterDetailsComponent.ViewState.Loaded
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterDetailsComponent.ViewState.LoadingFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState.OperationFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListDetailComponent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import thelordoftheringscharacterwiki.app.generated.resources.Res
import thelordoftheringscharacterwiki.app.generated.resources.character_list_with_details_screen_no_character_selected

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun CharacterListWithDetailsScreen(
    component: CharacterListDetailComponent,
    isActionsBottomSheetDisplayed: Boolean,
    isNameFilterDialogDisplayed: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    bottomSheetClosedAction: () -> Unit = {},
    hideNameFilterDialogAction: () -> Unit = {},
    showNameFilterDialogAction: () -> Unit = {},
) {
    val widthSizeClass = calculateWindowSizeClass().widthSizeClass
    val standardSpace = getStandardSpace(widthSizeClass)

    if (widthSizeClass != WindowWidthSizeClass.Expanded) {
        component.navigateToCharacterListOrDetails()
    }

    val viewState = component.viewState.collectAsStateWithLifecycle().value

    Row(
        horizontalArrangement = Arrangement.spacedBy(standardSpace),
        modifier = modifier.padding(
            start = standardSpace,
            end = standardSpace,
            bottom = standardSpace
        ),
    ) {
        CharacterListScreen(
            isActionsBottomSheetDisplayed = isActionsBottomSheetDisplayed,
            isNameFilterDialogDisplayed = isNameFilterDialogDisplayed,
            bottomSheetClosedAction = bottomSheetClosedAction,
            hideNameFilterDialogAction = hideNameFilterDialogAction,
            showCharacterDetailsAction = viewState::selectCharacter,
            showInformationAction = component::navigateToInformation,
            showNameFilterDialogAction = showNameFilterDialogAction,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(),
            viewState = viewState.characterListViewState,
        )

        viewState.characterDetailsViewState?.let {
            CompactCharacterDetailsScreen(
                character = (it as? Loaded)?.character,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(standardSpace),
            )
        } ?: NoCharacterSelectedScreen(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = standardSpace),
        )
    }

    when {
        viewState.characterListViewState is OperationFailed -> {
            (viewState.characterListViewState as? OperationFailed)?.let { errorViewState ->
                CharacterListErrorSnackbar(
                    retryAction = errorViewState::retryOperation,
                    snackbarHostState = snackbarHostState,
                )
            }
        }

        viewState.characterDetailsViewState is LoadingFailed -> {
            (viewState.characterDetailsViewState as? LoadingFailed)?.let { errorViewState ->
                CharacterLoadingErrorSnackbar(
                    retryAction = errorViewState::retryLoading,
                    snackbarHostState = snackbarHostState,
                )

            }
        }
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
internal fun NoCharacterSelectedScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier,
    ) {
        Text(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
            style = Material3Typography.headlineSmall,
            text = stringResource(
                Res.string.character_list_with_details_screen_no_character_selected,
            ),
        )
    }
}