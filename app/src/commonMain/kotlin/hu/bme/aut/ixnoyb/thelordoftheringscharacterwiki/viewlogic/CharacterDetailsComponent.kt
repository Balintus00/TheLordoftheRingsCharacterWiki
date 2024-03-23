package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext

interface CharacterDetailsComponent {

    fun navigateBack()

    fun navigateToCharacterListWithDetails()
}

class DefaultCharacterDetailsComponent(
    componentContext: ComponentContext,
    private val characterId: String,
    private val navigateBackAction: () -> Unit,
    private val navigateToCharacterListWithDetailsComponentAction: () -> Unit,
) : CharacterDetailsComponent, ComponentContext by componentContext {

    // TODO get character

    override fun navigateBack() {
        navigateBackAction()
    }

    override fun navigateToCharacterListWithDetails() {
        navigateToCharacterListWithDetailsComponentAction()
    }
}