package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme.Material3Typography
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.PlatformSpecificListScrollbar
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getStandardSpace
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import thelordoftheringscharacterwiki.app.generated.resources.Res
import thelordoftheringscharacterwiki.app.generated.resources.information_screen_content
import thelordoftheringscharacterwiki.app.generated.resources.information_screen_content_title
import thelordoftheringscharacterwiki.app.generated.resources.information_screen_top_app_bar_title
import thelordoftheringscharacterwiki.app.generated.resources.top_app_bar_navigation_icon_back_content_description

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
internal fun InformationScreenTopAppBar(
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
        title = { Text(stringResource(Res.string.information_screen_top_app_bar_title)) },
    )
}

@Composable
@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
internal fun InformationScreen(modifier: Modifier = Modifier) {
    Box {
        val standardSpace = getStandardSpace(calculateWindowSizeClass().widthSizeClass)
        val verticalScrollState = rememberScrollState()

        Column(
            modifier = modifier
                .verticalScroll(state = verticalScrollState)
                .padding(standardSpace),
            verticalArrangement = Arrangement.spacedBy(standardSpace),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = Material3Typography.headlineSmall,
                text = stringResource(Res.string.information_screen_content_title),
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = Material3Typography.bodyMedium,
                text = stringResource(Res.string.information_screen_content),
            )
        }

        PlatformSpecificListScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            scrollState = verticalScrollState,
        )
    }
}