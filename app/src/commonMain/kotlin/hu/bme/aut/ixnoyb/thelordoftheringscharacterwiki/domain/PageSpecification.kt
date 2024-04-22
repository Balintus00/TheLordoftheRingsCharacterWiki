package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import kotlin.jvm.JvmInline

data class PageSpecification(
    val number: PageNumber,
    val size: PageSize,
)

@JvmInline
value class PageNumber(val value: Int) {

    init {
        require(value > 0)
    }

    val isFirst: Boolean
        get() = value == 1

    fun getNextNumber(): PageNumber = PageNumber(this.value + 1)

    companion object {
        val FIRST_NUMBER = PageNumber(1)
    }
}

@JvmInline
value class PageSize(val value: Int) {

    init {
        require(value > 0)
    }
}