package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme.Material3Typography
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.PlatformSpecificListScrollbar
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.collectAsStateWithLifecycle
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getStandardSpace
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.placeholder
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterDetailsComponent
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterDetailsComponent.ViewState.Loaded
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterDetailsComponent.ViewState.LoadingFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model.Character
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import thelordoftheringscharacterwiki.app.generated.resources.Res
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_character_loading_failure_message
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_empty_character_attribute
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_birth
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_death
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_gender
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_hair
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_height
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_race
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_realm
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_section_label_spouse
import thelordoftheringscharacterwiki.app.generated.resources.character_details_screen_top_app_bar_title
import thelordoftheringscharacterwiki.app.generated.resources.snackbar_action_name_retry
import thelordoftheringscharacterwiki.app.generated.resources.top_app_bar_navigation_icon_back_content_description

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
fun CharacterDetailsTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    navigateBackAction: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateBackAction) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(
                        Res.string.top_app_bar_navigation_icon_back_content_description
                    )
                )
            }
        },
        scrollBehavior = scrollBehavior,
        title = { Text(stringResource(Res.string.character_details_screen_top_app_bar_title)) },
    )
}

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun CharacterDetailsScreen(
    component: CharacterDetailsComponent,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val viewState = component.viewState.collectAsStateWithLifecycle().value
    val windowWidthSizeClass = calculateWindowSizeClass().widthSizeClass

    when (windowWidthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            component.navigateToCharacterListWithDetails()
        }

        WindowWidthSizeClass.Compact -> {
            CompactCharacterDetailsScreen(
                character = (viewState as? Loaded)?.character,
                modifier = modifier.padding(getStandardSpace(WindowWidthSizeClass.Compact)),
            )
        }

        WindowWidthSizeClass.Medium -> {
            MediumCharacterDetailsScreen(
                character = (viewState as? Loaded)?.character,
                modifier = modifier,
            )
        }
    }

    if (viewState is LoadingFailed) {
        CharacterLoadingErrorSnackbar(
            retryAction = viewState::retryLoading,
            snackbarHostState = snackbarHostState,
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun CompactCharacterDetailsScreen(
    character: Character?,
    modifier: Modifier = Modifier,
) {
    Box {
        val verticalScrollState = rememberScrollState(0)

        Column(
            modifier = modifier.verticalScroll(verticalScrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(isVisible = character == null),
                style = Material3Typography.headlineSmall,
                text = character?.name.normalizeCharacterAttribute(),
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_height),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.height.normalizeCharacterAttribute(),
                )
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_race),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.race.normalizeCharacterAttribute(),
                )
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_gender)
                        .normalizeCharacterAttribute(),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.gender.normalizeCharacterAttribute(),
                )
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_birth),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.birth.normalizeCharacterAttribute(),
                )
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_spouse),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.spouse.normalizeCharacterAttribute(),
                )
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_death),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.death.normalizeCharacterAttribute(),
                )
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_realm),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.realm.normalizeCharacterAttribute(),
                )
                CompactLabeledCharacterAttribute(
                    label = stringResource(Res.string.character_details_screen_section_label_hair),
                    modifier = Modifier.fillMaxWidth(),
                    value = character?.hair.normalizeCharacterAttribute(),
                )
            }
        }

        PlatformSpecificListScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            scrollState = verticalScrollState,
        )
    }
}

@Composable
fun CompactLabeledCharacterAttribute(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            style = Material3Typography.labelMedium,
            text = label,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(isVisible = value == null),
            style = Material3Typography.bodyMedium,
            text = value ?: "",
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
private fun String?.normalizeCharacterAttribute(): String = if (isNullOrBlank()) {
    stringResource(Res.string.character_details_screen_empty_character_attribute)
} else {
    this
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun MediumCharacterDetailsScreen(
    character: Character?,
    modifier: Modifier = Modifier,
) {
    val verticalScrollState = rememberScrollState()
    val standardSpace = getStandardSpace(WindowWidthSizeClass.Medium)

    Box {
        Column(
            modifier = modifier
                .verticalScroll(verticalScrollState)
                .padding(standardSpace),
            verticalArrangement = Arrangement.spacedBy(36.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(isVisible = character == null),
                style = Material3Typography.headlineSmall,
                text = character?.name.normalizeCharacterAttribute(),
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                MediumDoubleLabeledCharacterAttribute(
                    leftLabel = stringResource(
                        Res.string.character_details_screen_section_label_height
                    ),
                    leftValue = character?.height,
                    rightLabel = stringResource(
                        Res.string.character_details_screen_section_label_race
                    ),
                    rightValue = character?.race,
                    modifier = Modifier.fillMaxWidth(),
                )
                MediumDoubleLabeledCharacterAttribute(
                    leftLabel = stringResource(
                        Res.string.character_details_screen_section_label_gender
                    ),
                    leftValue = character?.gender,
                    rightLabel = stringResource(
                        Res.string.character_details_screen_section_label_birth
                    ),
                    rightValue = character?.birth,
                    modifier = Modifier.fillMaxWidth(),
                )
                MediumDoubleLabeledCharacterAttribute(
                    leftLabel = stringResource(
                        Res.string.character_details_screen_section_label_spouse
                    ),
                    leftValue = character?.spouse,
                    rightLabel = stringResource(
                        Res.string.character_details_screen_empty_character_attribute
                    ),
                    rightValue = character?.death,
                    modifier = Modifier.fillMaxWidth(),
                )
                MediumDoubleLabeledCharacterAttribute(
                    leftLabel = stringResource(
                        Res.string.character_details_screen_section_label_realm
                    ),
                    leftValue = character?.realm,
                    rightLabel = stringResource(
                        Res.string.character_details_screen_section_label_hair
                    ),
                    rightValue = character?.hair,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        PlatformSpecificListScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            scrollState = verticalScrollState,
        )
    }
}

@Composable
fun MediumDoubleLabeledCharacterAttribute(
    leftLabel: String,
    leftValue: String?,
    rightLabel: String,
    rightValue: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.5f),
                style = Material3Typography.labelMedium,
                text = leftLabel,
            )
            Text(
                modifier = Modifier.fillMaxWidth(0.5f),
                style = Material3Typography.labelMedium,
                text = rightLabel,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .placeholder(isVisible = leftValue == null),
                style = Material3Typography.bodyMedium,
                text = leftValue.normalizeCharacterAttribute(),
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .placeholder(isVisible = rightValue == null),
                style = Material3Typography.bodyMedium,
                text = rightValue.normalizeCharacterAttribute(),
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CharacterLoadingErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    retryAction: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    val errorSnackbarAction = stringResource(
        Res.string.snackbar_action_name_retry
    )
    val errorSnackbarMessage = stringResource(
        Res.string.character_details_screen_character_loading_failure_message
    )

    coroutineScope.launch {
        val snackbarResult = snackbarHostState.showSnackbar(
            actionLabel = errorSnackbarAction,
            message = errorSnackbarMessage,
            withDismissAction = true,
        )

        if (snackbarResult == SnackbarResult.ActionPerformed) {
            retryAction()
        }
    }
}