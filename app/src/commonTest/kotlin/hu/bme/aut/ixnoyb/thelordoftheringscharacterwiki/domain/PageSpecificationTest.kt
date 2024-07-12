package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

@Suppress("unused")
class PageSpecificationTest : BehaviorSpec({

    Context("PageNumber holds proper state and throws exception with invalid arguments") {

        Given("-1 value") {
            val number = -1

            When("PageNumber is created") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        PageNumber(number)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                        t.should { it is IllegalArgumentException }
                    }
                }
            }
        }

        Given("0 value") {
            val number = 0
            When("PageNumber is created") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        PageNumber(number)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                        t should { t is IllegalArgumentException }
                    }
                }
            }
        }

        Given("1 value") {
            val number = 1

            When("PageNumber is created") {
                val pageNumber = PageNumber(number)

                Then("PageNumber should contain the same number") {
                    pageNumber.value shouldBe number
                }
            }
        }

        Given("42069 value") {
            val number = 42069

            When("PageNumber is created") {
                val pageNumber = PageNumber(number)

                Then("PageNumber should contain the same number") {
                    pageNumber.value shouldBe number
                }
            }
        }
    }

    Context("FIRST_NUMBER PageNumber behaves properly") {

        Given("first page number") {
            val pageNumber = PageNumber.FIRST_NUMBER

            When("isFirst is called") {
                val result = pageNumber.isFirst

                Then("result should be true") {
                    result shouldBe true
                }
            }

            When("getNextNumber is called") {
                val result = pageNumber.getNextNumber()

                Then("result should be second page number") {
                    result shouldBe PageNumber(2)
                }
            }
        }
    }

    Context("PageSize holds proper state and throws exception with invalid arguments") {

        Given("-1 value") {
            val number = -1

            When("PageSize is created") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        PageSize(number)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                        t.should { it is IllegalArgumentException }
                    }
                }
            }
        }

        Given("0 value") {
            val number = 0
            When("PageSize is created") {

                Then("IllegalArgumentException should be thrown") {
                    try {
                        PageSize(number)
                        fail("Exception expected")
                    } catch (t: Throwable) {
                        t.should { it is IllegalArgumentException }
                    }
                }
            }
        }

        Given("1 value") {
            val number = 1

            When("PageSize is created") {
                val size = PageSize(number)

                Then("PageSize should contain the same number") {
                    size.value shouldBe number
                }
            }
        }

        Given("69420 value") {
            val number = 69420

            When("PageSize is created") {
                val size = PageSize(number)

                Then("PageSize should contain the same number") {
                    size.value shouldBe number
                }
            }
        }
    }

    Context("PageSpecification holds proper state") {

        Given("a PageNumber and PageSize") {
            val number = PageNumber.FIRST_NUMBER
            val size = PageSize(1)

            When("PageSpecification is created") {
                val specification = PageSpecification(number, size)

                Then("specification should contain the same number") {
                    specification.number shouldBe number
                }

                Then("specification should contain the same size") {
                    specification.size shouldBe size
                }
            }
        }
    }
})