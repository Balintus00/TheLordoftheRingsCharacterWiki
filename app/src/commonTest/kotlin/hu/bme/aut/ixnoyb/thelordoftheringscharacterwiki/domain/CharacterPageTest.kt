package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.testutility.createDomainCharacter
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class CharacterPageTest : BehaviorSpec({

    Context("CharacterPage holds proper state") {

        Given("list of characters and information that next page exists") {
            val characters = listOf(createDomainCharacter())
            val isNextPageExist = true

            When("CharacterPage is created") {
                val page = CharacterPage(characters, isNextPageExist)

                Then("CharacterPage should contain the same attributes") {
                    page.characters shouldBe characters
                    page.isNextPageExist shouldBe isNextPageExist
                }
            }
        }
    }
})