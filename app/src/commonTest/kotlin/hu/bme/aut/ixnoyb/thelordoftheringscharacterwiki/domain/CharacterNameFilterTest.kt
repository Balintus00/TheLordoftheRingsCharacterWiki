package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

@Suppress("unused")
class CharacterNameFilterTest : BehaviorSpec({

    Context(
        "CharacterNameFilter holds proper state and throws proper exceptions " +
                "if creation arguments are wrong"
    ) {

        Given("an empty string") {
            val filterCandidate = ""

            When("CharacterNameFilter is created with value") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        CharacterNameFilter(filterCandidate)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                     t should { t is IllegalArgumentException }
                    }
                }
            }
        }

        Given("a string that contains whitespaces") {
            val filterCandidate = "   "

            When("CharacterNameFilter is created with value") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        CharacterNameFilter(filterCandidate)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                        t should { t is IllegalArgumentException }
                    }
                }
            }
        }

        Given("a string that contains line separator") {
            val filterCandidate = "\n"

            When("CharacterNameFilter is created with value") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        CharacterNameFilter(filterCandidate)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                        t should { t is IllegalArgumentException }
                    }
                }
            }
        }

        Given("a string with 1 length") {
            val filterCandidate = "a"

            When("CharacterNameFilter is created with value") {
                val filter = CharacterNameFilter(filterCandidate)

                Then("CharacterNameFilter should contain the same values") {
                    filter.value shouldBe filterCandidate
                }
            }
        }

        Given("a string with 16 length with special characters") {
            val filterCandidate = " áR4górN\n ヤギ\uD83D\uDE0A!#"

            When("CharacterNameFilter is created with values") {
                val filter = CharacterNameFilter(filterCandidate)

                Then("CharacterNameFilter should contain the same value") {
                    filter.value shouldBe filterCandidate
                }
            }
        }

        Given("a string with 32 length") {
            val filterCandidate = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

            When("CharacterNameFilter is created with value") {
                val filter = CharacterNameFilter(filterCandidate)

                Then("CharacterNameFilter should contain the same values") {
                    filter.value shouldBe filterCandidate
                }
            }
        }

        Given("a string with 33 length") {
            val filterCandidate = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

            When("CharacterNameFilter is created with value") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        CharacterNameFilter(filterCandidate)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                        t should { t is IllegalArgumentException }
                    }
                }
            }
        }
    }
})