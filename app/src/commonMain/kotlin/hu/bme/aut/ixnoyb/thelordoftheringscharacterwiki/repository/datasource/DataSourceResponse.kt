package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource

internal sealed interface DataSourceCommandResponse

internal data object DataSourceCommandSuccess : DataSourceCommandResponse

internal data class DataSourceFailure(
    val cause: Throwable? = null,
) : DataSourceQueryResponse<Nothing>, DataSourceCommandResponse

internal sealed interface DataSourceQueryResponse<out T>

internal data class DataSourceQuerySuccess<T>(val result: T) : DataSourceQueryResponse<T>