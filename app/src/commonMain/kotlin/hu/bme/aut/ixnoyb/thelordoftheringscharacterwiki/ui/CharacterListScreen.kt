package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme.Material3Typography
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.BasicDialogWithClassicalLayout
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.PlatformSpecificListScrollbar
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.collectAsStateWithLifecycle
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getStandardSpace
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.placeholder
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState.CharactersAvailable
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState.FirstPageLoading
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterListComponent.ViewState.OperationFailed
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.model.CharacterListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import thelordoftheringscharacterwiki.app.generated.resources.Res
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_action_filter_by_name
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_action_information
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_character_list_loading_failure_message
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_empty_result_text
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_remove_filter_content_description
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_top_app_bar_icon_content_description
import thelordoftheringscharacterwiki.app.generated.resources.character_list_screen_top_app_bar_title
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_action_cancel
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_action_filter
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_input_label
import thelordoftheringscharacterwiki.app.generated.resources.filter_character_by_name_dialog_title
import thelordoftheringscharacterwiki.app.generated.resources.snackbar_action_name_retry

@Composable
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalResourceApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
internal fun CharacterListTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    filterAction: () -> Unit = {},
    informationAction: () -> Unit = {},
    showCompactWidthActionsAction: () -> Unit = {},
) {
    var isActionsMenuExpanded by remember { mutableStateOf(false) }
    val windowWidthSizeClass = calculateWindowSizeClass().widthSizeClass

    CenterAlignedTopAppBar(
        actions = {
            IconButton(
                onClick = {
                    if (windowWidthSizeClass == Compact) {
                        showCompactWidthActionsAction()
                    } else {
                        isActionsMenuExpanded = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(
                        Res.string.character_list_screen_top_app_bar_icon_content_description
                    ),
                )
            }
            if (windowWidthSizeClass != Compact) {
                MoreActionsDropdownMenu(
                    isMenuExpanded = isActionsMenuExpanded,
                    closeMenuAction = { isActionsMenuExpanded = false },
                    onFilterMenuItemClickedAction = filterAction,
                    onInformationMenuItemClickedAction = informationAction,
                )
            } else {
                isActionsMenuExpanded = false
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = { Text(stringResource(Res.string.character_list_screen_top_app_bar_title)) },
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun MoreActionsDropdownMenu(
    isMenuExpanded: Boolean,
    closeMenuAction: () -> Unit = {},
    onInformationMenuItemClickedAction: () -> Unit = {},
    onFilterMenuItemClickedAction: () -> Unit = {},
) {
    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = closeMenuAction,
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                )
            },
            onClick = onInformationMenuItemClickedAction,
            text = { Text(stringResource(Res.string.character_list_screen_action_information)) },
        )
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = null,
                )
            },
            onClick = onFilterMenuItemClickedAction,
            text = { Text(stringResource(Res.string.character_list_screen_action_filter_by_name)) },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
internal fun CharacterListScreen(
    component: CharacterListComponent,
    isActionsBottomSheetDisplayed: Boolean,
    snackbarHostState: SnackbarHostState,
    isNameFilterDialogDisplayed: Boolean,
    modifier: Modifier = Modifier,
    bottomSheetClosedAction: () -> Unit = {},
    hideNameFilterDialogAction: () -> Unit = {},
    showNameFilterDialogAction: () -> Unit = {},
) {
    val windowWidthSizeClass = calculateWindowSizeClass().widthSizeClass

    if (windowWidthSizeClass == WindowWidthSizeClass.Expanded) {
        component.navigateToCharacterListWithDetails()
    }

    val viewState = component.viewState.collectAsStateWithLifecycle().value

    CharacterListScreen(
        viewState = viewState,
        isActionsBottomSheetDisplayed = isActionsBottomSheetDisplayed,
        isNameFilterDialogDisplayed = isNameFilterDialogDisplayed,
        modifier = modifier,
        bottomSheetClosedAction = bottomSheetClosedAction,
        hideNameFilterDialogAction = hideNameFilterDialogAction,
        showCharacterDetailsAction = { component.navigateToCharacterDetails(it) },
        showInformationAction = component::navigateToInformation,
        showNameFilterDialogAction = showNameFilterDialogAction,
    )

    if (viewState is OperationFailed) {
        CharacterListErrorSnackbar(
            retryAction = viewState::retryOperation,
            snackbarHostState = snackbarHostState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CharacterListScreen(
    viewState: ViewState,
    isActionsBottomSheetDisplayed: Boolean,
    isNameFilterDialogDisplayed: Boolean,
    modifier: Modifier = Modifier,
    bottomSheetClosedAction: () -> Unit = {},
    hideNameFilterDialogAction: () -> Unit = {},
    showCharacterDetailsAction: (String) -> Unit = {},
    showInformationAction: () -> Unit = {},
    showNameFilterDialogAction: () -> Unit = {},
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    when (viewState) {
        is FirstPageLoading -> {
            FirstPageLoading(
                modifier = modifier,
            )
        }

        is CharactersAvailable -> {
            CharacterList(
                activeNameFilter = viewState.currentNameFilter,
                characters = viewState.characters,
                isLoadingNextPage = viewState.isNextPageLoading,
                isRefreshing = viewState.isRefreshing,
                modifier = modifier,
                clearFilterAction = { viewState.applyFilter(null) },
                loadNextPageAction = { viewState.loadNextPage() },
                onItemClickedAction = { showCharacterDetailsAction(it.id) },
                refreshListAction = { viewState.refresh() },
                showFilterDialogAction = showNameFilterDialogAction,
            )
        }
    }

    if (isActionsBottomSheetDisplayed) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            modifier = Modifier.height(174.dp),
            onDismissRequest = bottomSheetClosedAction,
        ) {
            ActionModalBottomSheetContent(
                bottomSheetClosedAction = bottomSheetClosedAction,
                bottomSheetState = bottomSheetState,
                filterActionItemOnClickAction = showNameFilterDialogAction,
                informationActionItemOnClickAction = showInformationAction,
                modifier = Modifier.fillMaxWidth(),
                scope = scope,
            )
        }
    }

    if (isNameFilterDialogDisplayed) {
        BasicAlertDialog(
            onDismissRequest = hideNameFilterDialogAction,
        ) {
            FilterByNameDialogContent(
                initialNameFilter = viewState.currentNameFilter,
                filterMaximumLength = CharacterListComponent.CHARACTER_NAME_FILTER_MAXIMUM_LENGTH,
                cancelAction = hideNameFilterDialogAction,
                filterByNameAction = {
                    viewState.applyFilter(it)
                    hideNameFilterDialogAction()
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
internal fun FirstPageLoading(modifier: Modifier) {
    val standardSpace = getStandardSpace(calculateWindowSizeClass().widthSizeClass)

    Column(
        modifier = modifier.padding(standardSpace),
        verticalArrangement = Arrangement.spacedBy(standardSpace),
    ) {
        repeat(3) {
            CharacterListItem(
                characterListItem = null,
                modifier = Modifier.fillMaxWidth(),
                onClickAction = null,
            )
        }
    }
}

@Composable
internal fun CharacterListItem(
    characterListItem: CharacterListItem?,
    modifier: Modifier = Modifier,
    onClickAction: ((CharacterListItem) -> Unit)?,
) {
    val itemModifier = if (onClickAction != null && characterListItem != null) {
        modifier.clickable { onClickAction(characterListItem) }
    } else {
        modifier
    }

    Column(
        modifier = itemModifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(isVisible = characterListItem == null),
            style = Material3Typography.bodyLarge,
            text = characterListItem?.name ?: "",
            textAlign = TextAlign.Center,
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(isVisible = characterListItem == null),
        )
    }
}

@Composable
@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class,
    ExperimentalResourceApi::class
)
internal fun CharacterList(
    activeNameFilter: String?,
    characters: List<CharacterListItem>,
    isLoadingNextPage: Boolean,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    clearFilterAction: () -> Unit,
    loadNextPageAction: () -> Unit,
    onItemClickedAction: (CharacterListItem) -> Unit,
    refreshListAction: () -> Unit,
    showFilterDialogAction: () -> Unit,
) {
    val listState = rememberLazyListState()
    val isCharacterListBottomReached: Boolean by remember {
        derivedStateOf {
            val bottomThreshold = 3
            val lastVisibleListItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleListItem != null && (
                    lastVisibleListItem.index >
                            (listState.layoutInfo.totalItemsCount - bottomThreshold)
                    )
        }
    }
    val pullToRefreshState = rememberPullToRefreshState()

    val standardSpace = getStandardSpace(calculateWindowSizeClass().widthSizeClass)

    LaunchedEffect(isCharacterListBottomReached) {
        if (isCharacterListBottomReached) loadNextPageAction()
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) { refreshListAction() }
    }

    if (isRefreshing) {
        pullToRefreshState.startRefresh()
    } else {
        pullToRefreshState.endRefresh()
    }

    Box(modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            contentPadding = PaddingValues(standardSpace),
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(standardSpace),
        ) {
            if (activeNameFilter != null) {
                item {
                    ActiveCharacterNameFilterInputChip(
                        activeNameFilter = activeNameFilter,
                        clearFilterAction = clearFilterAction,
                        showFilterDialogAction = showFilterDialogAction,
                    )
                }
            }

            if (characters.isNotEmpty()) {
                items(
                    items = characters,
                    key = { it.id }
                ) {
                    CharacterListItem(
                        characterListItem = it,
                        modifier = Modifier.fillMaxWidth(),
                        onClickAction = onItemClickedAction,
                    )
                }
            } else {
                item {
                    Text(
                        modifier = modifier.fillMaxWidth(),
                        style = Material3Typography.headlineSmall,
                        text = stringResource(Res.string.character_list_screen_empty_result_text),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            if (isLoadingNextPage) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )

        PlatformSpecificListScrollbar(
            listState = listState,
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ActiveCharacterNameFilterInputChip(
    activeNameFilter: String,
    modifier: Modifier = Modifier,
    clearFilterAction: () -> Unit = {},
    showFilterDialogAction: () -> Unit = {},
) {
    InputChip(
        label = { Text(activeNameFilter) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = null,
            )
        },
        modifier = modifier,
        onClick = { showFilterDialogAction() },
        selected = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(
                    Res.string.character_list_screen_remove_filter_content_description
                ),
                modifier = Modifier.clickable { clearFilterAction() },
            )
        },
    )
}

@Composable
@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
internal fun ActionModalBottomSheetContent(
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
internal fun FilterByNameDialogContent(
    initialNameFilter: String?,
    filterMaximumLength: Int,
    cancelAction: () -> Unit = {},
    filterByNameAction: (String) -> Unit = {},
) {
    var nameToFilter by remember { mutableStateOf(initialNameFilter ?: "") }

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
            OutlinedTextField(
                label = {
                    Text(stringResource(Res.string.filter_character_by_name_dialog_input_label))
                },
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { nameToFilter = it.take(filterMaximumLength) },
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
internal fun BottomSheetActionItem(
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

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun CharacterListErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    retryAction: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    val errorSnackbarAction = stringResource(
        Res.string.snackbar_action_name_retry
    )
    val errorSnackbarMessage = stringResource(
        Res.string.character_list_screen_character_list_loading_failure_message
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