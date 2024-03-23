package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.viewlogic

import com.arkivanov.decompose.ComponentContext

interface InformationComponent {

    fun navigateBack()
}

class DefaultInformationComponent(
    componentContext: ComponentContext,
    private val navigateBackAction: () -> Unit,
) : InformationComponent, ComponentContext by componentContext {

    override fun navigateBack() {
        navigateBackAction()
    }
}