package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme.Material3Typography
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.BasicDialogWithClassicalLayout
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import thelordoftheringscharacterwiki.app.generated.resources.Res
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_action_filter_by_name
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_action_information
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_top_app_bar_icon_content_description
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_top_app_bar_title
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_action_cancel
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_action_filter
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_input_label
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_title

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
fun CharacterListTopAppBar(modifier: Modifier = Modifier, showActionsAction: () -> Unit = {}) {
    CenterAlignedTopAppBar(
        actions = {
            IconButton(onClick = showActionsAction) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(
                        Res.string.character_list_screen_top_app_bar_icon_content_description
                    ),
                )
            }
        },
        modifier = modifier,
        title = { Text(stringResource(Res.string.character_list_screen_top_app_bar_title)) },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun CharacterListScreen(
    component: CharacterListComponent,
    isBottomSheetDisplayed: Boolean,
    modifier: Modifier = Modifier,
    bottomSheetClosedAction: () -> Unit = {},
) {
    if (calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Expanded) {
        component.navigateToCharacterListWithDetails()
    }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isDialogDisplayed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // TODO create proper content
    Button(
        onClick = {
            component.navigateToCharacterDetails("1")
        },
    ) {
        Text("To details")
    }

    if (isBottomSheetDisplayed) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            modifier = Modifier.height(174.dp),
            onDismissRequest = bottomSheetClosedAction,
        ) {
            ActionModalBottomSheetContent(
                bottomSheetClosedAction = bottomSheetClosedAction,
                bottomSheetState = bottomSheetState,
                filterActionItemOnClickAction = { isDialogDisplayed = true },
                informationActionItemOnClickAction = component::navigateToInformation,
                modifier = Modifier.fillMaxWidth(),
                scope = scope,
            )
        }
    }

    if (isDialogDisplayed) {
        BasicAlertDialog(
            onDismissRequest = {
                isDialogDisplayed = false
            },
        ) {
            FilterByNameDialogContent(
                cancelAction = { isDialogDisplayed = false },
                filterByNameAction = {
                    // TODO filter by name
                    isDialogDisplayed = false
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
fun ActionModalBottomSheetContent(
    bottomSheetState: SheetState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    bottomSheetClosedAction: () -> Unit = {},
    informationActionItemOnClickAction: () -> Unit = {},
    filterActionItemOnClickAction: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        BottomSheetActionItem(
            iconImage = Icons.Default.Info,
            modifier = Modifier.fillMaxWidth(),
            onClickAction = {
                hideBottomSheetAction(bottomSheetState, scope) {
                    informationActionItemOnClickAction()
                    bottomSheetClosedAction()
                }
            },
            text = stringResource(Res.string.character_list_screen_action_information),
        )
        BottomSheetActionItem(
            iconImage = Icons.Default.FilterAlt,
            modifier = Modifier.fillMaxWidth(),
            onClickAction = {
                filterActionItemOnClickAction()
                hideBottomSheetAction(bottomSheetState, scope, bottomSheetClosedAction)
            },
            text = stringResource(Res.string.character_list_screen_action_filter_by_name),
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun FilterByNameDialogContent(
    cancelAction: () -> Unit = {},
    filterByNameAction: (String) -> Unit = {},
) {
    var nameToFilter by remember { mutableStateOf("") }

    BasicDialogWithClassicalLayout(
        topContentColumn = {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null,
            )
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                style = Material3Typography.headlineSmall,
                text = stringResource(Res.string.filter_character_by_name_dialog_title),
                textAlign = TextAlign.Center,
            )
            // TODO handle max length
            OutlinedTextField(
                label = {
                    Text(stringResource(Res.string.filter_character_by_name_dialog_input_label))
                },
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { nameToFilter = it },
                singleLine = true,
                value = nameToFilter,
            )
        },
        bottomActionRow = {
            TextButton(
                onClick = cancelAction,
            ) {
                Text(
                    text = stringResource(
                        Res.string.filter_character_by_name_dialog_action_cancel
                    ),
                )
            }
            TextButton(
                onClick = { filterByNameAction(nameToFilter) },
            ) {
                Text(
                    text = stringResource(
                        Res.string.filter_character_by_name_dialog_action_filter
                    ),
                )
            }
        },
    )
}

@Composable
fun BottomSheetActionItem(
    iconImage: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClickAction: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .height(56.dp)
            .clickable { onClickAction() }
            .padding(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = iconImage,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun hideBottomSheetAction(
    sheetState: SheetState,
    scope: CoroutineScope,
    bottomSheetClosedAction: () -> Unit
) {
    scope.launch { sheetState.hide() }
        .invokeOnCompletion {
            if (sheetState.isVisible.not()) {
                bottomSheetClosedAction()
            }
        }
}