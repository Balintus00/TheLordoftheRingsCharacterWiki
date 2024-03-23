package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext

interface CharacterListWithDetailsComponent {

    fun navigateToCharacterListOrDetails()

    fun navigateToInformationComponent()

    fun selectCharacter(characterId: String)
}

class DefaultCharacterListWithDetailsComponent(
    componentContext: ComponentContext,
    private val navigateToCharacterListOrDetailsComponent: (String?) -> Unit,
    private val navigateToInformationComponentAction: () -> Unit,
    private var selectedCharacterId: String?,
) : CharacterListWithDetailsComponent, ComponentContext by componentContext {

    override fun navigateToCharacterListOrDetails() {
        navigateToCharacterListOrDetailsComponent(selectedCharacterId)
    }

    override fun navigateToInformationComponent() {
        navigateToInformationComponentAction()
    }

    override fun selectCharacter(characterId: String) {
        selectedCharacterId = characterId
    }
}