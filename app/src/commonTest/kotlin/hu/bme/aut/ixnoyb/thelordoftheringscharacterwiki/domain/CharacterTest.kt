package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class CharacterTest : BehaviorSpec({

    Context("Character holds proper state") {
        Given(
            "an ID, birth, death, gender, hair, height, name, race, realm " +
                    "and spouse attributes"
        ) {
            val id = ID("1")
            val birth = Birth("Some place")
            val death = Death("Somewhere")
            val gender = Gender("Male")
            val hair = Hair("Black")
            val height = Height("Height")
            val name = Name("Name")
            val race = Race("Human")
            val realm = Realm("Middle earth")
            val spouse = Spouse("Female Name")

            When("character is created") {
                val character = Character(
                    id, birth, death, gender, hair, height, name, race, realm, spouse,
                )

                Then("character should have the same ID") {
                    character.id shouldBe id
                }

                Then("character should have the same birth") {
                    character.birth shouldBe birth
                }

                Then("character should have the same death") {
                    character.death shouldBe death
                }

                Then("character should have the same gender") {
                    character.gender shouldBe gender
                }

                Then("character should have the same hair") {
                    character.hair shouldBe hair
                }

                Then("character should have the same height") {
                    character.height shouldBe height
                }

                Then("character should have the same name") {
                    character.name shouldBe name
                }

                Then("character should have the same race") {
                    character.race shouldBe race
                }

                Then("character should have the same realm") {
                    character.realm shouldBe realm
                }

                Then("character should have the same spouse") {
                    character.spouse shouldBe spouse
                }
            }
        }
    }
})