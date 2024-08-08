package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Character(
    @SerialName("_id") val id : String,
    val birth: String?,
    val death: String?,
    val gender: String?,
    val hair: String?,
    val height: String?,
    val name: String?,
    val race: String?,
    val realm: String?,
    val spouse: String?,
)