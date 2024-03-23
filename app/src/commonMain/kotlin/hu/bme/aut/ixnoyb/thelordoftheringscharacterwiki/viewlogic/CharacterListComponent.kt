package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext

interface CharacterListComponent {

    fun navigateToCharacterDetails(characterId: String)

    fun navigateToCharacterListWithDetails()

    fun navigateToInformation()
}

class DefaultCharacterListComponent(
    componentContext: ComponentContext,
    private val navigateToCharacterDetailsComponentAction: (String) -> Unit,
    private val navigateToCharacterListWithDetailsComponentAction: () -> Unit,
    private val navigateToInformationComponentAction: () -> Unit,
) : CharacterListComponent, ComponentContext by componentContext {

    override fun navigateToCharacterDetails(characterId: String) {
        navigateToCharacterDetailsComponentAction(characterId)
    }

    override fun navigateToCharacterListWithDetails() {
        navigateToCharacterListWithDetailsComponentAction()
    }

    override fun navigateToInformation() {
        navigateToInformationComponentAction()
    }
}