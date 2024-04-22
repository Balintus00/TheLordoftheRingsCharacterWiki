package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MediumDoubleLabeledCharacterAttributePreview() {
    MediumDoubleLabeledCharacterAttribute(
        leftLabel = "Left Label",
        leftValue = "Left Value",
        rightLabel = "Right Label",
        rightValue = "Right Value",
    )
}