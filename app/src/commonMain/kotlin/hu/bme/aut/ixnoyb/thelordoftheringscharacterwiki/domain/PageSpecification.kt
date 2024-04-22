package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain

import kotlin.jvm.JvmInline

internal data class PageSpecification(
    val number: PageNumber,
    val size: PageSize,
)

@JvmInline
internal value class PageNumber(val value: Int) {

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
internal value class PageSize(val value: Int) {

    init {
        require(value > 0)
    }
}