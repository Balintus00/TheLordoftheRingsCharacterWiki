package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.theme.Material3Typography
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui.utility.getStandardSpace
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic.CharacterDetailsComponent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import thelordoftheringscharacterwiki.app.generated.resources.Res
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
    modifier: Modifier = Modifier,
) {
    val windowWidthSizeClass = calculateWindowSizeClass().widthSizeClass

    // TODO display proper data
    when (windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CompactCharacterDetailsScreen(
                birth = "TODO()",
                death = "TODO()",
                gender = "TODO()",
                hair = "TODO()",
                height = "TODO()",
                name = "TODO()",
                race = "TODO()",
                realm = "TODO()",
                spouse = "TODO()",
                modifier = modifier,
            )
        }

        WindowWidthSizeClass.Medium -> {
            MediumCharacterDetailsScreen(
                birth = "TODO()",
                death = "TODO()",
                gender = "TODO()",
                hair = "TODO()",
                height = "TODO()",
                name = "TODO()",
                race = "TODO()",
                realm = "TODO()",
                spouse = "TODO()",
                modifier = modifier,
            )
        }

        else -> {
            component.navigateToCharacterListWithDetails()
        }
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun CompactCharacterDetailsScreen(
    birth: String,
    death: String,
    gender: String,
    hair: String,
    height: String,
    name: String,
    race: String,
    realm: String,
    spouse: String,
    modifier: Modifier = Modifier,
) {
    val standardSpace = getStandardSpace(WindowWidthSizeClass.Compact)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(standardSpace),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            style = Material3Typography.headlineSmall,
            text = name,
            textAlign = TextAlign.Center,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_height)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = height,
            )
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_race)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = race,
            )
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_gender)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = gender,
            )
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_birth)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = birth,
            )
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_spouse)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = spouse,
            )
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_death)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = death,
            )
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_realm)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = realm,
            )
            CompactLabeledCharacterAttribute(
                label = stringResource(Res.string.character_details_screen_section_label_hair)
                    .normalizeCharacterAttribute(),
                modifier = Modifier.fillMaxWidth(),
                value = hair,
            )
        }
    }
}

@Composable
fun CompactLabeledCharacterAttribute(
    label: String,
    value: String,
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
            modifier = Modifier.fillMaxWidth(),
            style = Material3Typography.bodyMedium,
            text = value,
        )
    }
}

@Composable
@OptIn(ExperimentalResourceApi::class)
private fun String.normalizeCharacterAttribute(): String = ifBlank {
    stringResource(Res.string.character_details_screen_empty_character_attribute)
}

@Composable
@OptIn(ExperimentalResourceApi::class)
fun MediumCharacterDetailsScreen(
    birth: String,
    death: String,
    gender: String,
    hair: String,
    height: String,
    name: String,
    race: String,
    realm: String,
    spouse: String,
    modifier: Modifier = Modifier,
) {
    val standardSpace = getStandardSpace(WindowWidthSizeClass.Medium)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(standardSpace),
        verticalArrangement = Arrangement.spacedBy(36.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            style = Material3Typography.headlineSmall,
            text = name,
            textAlign = TextAlign.Center,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            MediumDoubleLabeledCharacterAttribute(
                leftLabel = stringResource(Res.string.character_details_screen_section_label_height),
                leftValue = height,
                rightLabel = stringResource(Res.string.character_details_screen_section_label_race),
                rightValue = race,
                modifier = Modifier.fillMaxWidth(),
            )
            MediumDoubleLabeledCharacterAttribute(
                leftLabel = stringResource(Res.string.character_details_screen_section_label_gender),
                leftValue = gender,
                rightLabel = stringResource(Res.string.character_details_screen_section_label_birth),
                rightValue = birth,
                modifier = Modifier.fillMaxWidth(),
            )
            MediumDoubleLabeledCharacterAttribute(
                leftLabel = stringResource(Res.string.character_details_screen_section_label_spouse),
                leftValue = spouse,
                rightLabel = stringResource(Res.string.character_details_screen_empty_character_attribute),
                rightValue = death,
                modifier = Modifier.fillMaxWidth(),
            )
            MediumDoubleLabeledCharacterAttribute(
                leftLabel = stringResource(Res.string.character_details_screen_section_label_realm),
                leftValue = realm,
                rightLabel = stringResource(Res.string.character_details_screen_section_label_hair),
                rightValue = hair,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun MediumDoubleLabeledCharacterAttribute(
    leftLabel: String,
    leftValue : String,
    rightLabel: String,
    rightValue: String,
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
                modifier = Modifier.fillMaxWidth(),
                style = Material3Typography.labelMedium,
                text = leftLabel,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = Material3Typography.labelMedium,
                text = rightLabel,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = Material3Typography.bodyMedium,
                text = leftValue,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                style = Material3Typography.bodyMedium,
                text = rightValue,
            )
        }
    }
}