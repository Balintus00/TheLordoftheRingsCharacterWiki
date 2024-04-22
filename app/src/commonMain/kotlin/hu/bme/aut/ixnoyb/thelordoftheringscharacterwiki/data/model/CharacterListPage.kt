package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CharacterListPage(
    @SerialName("docs") val characters: List<Character>,
    @SerialName("limit") val pageSize: Int,
    @SerialName("page") val currentPage: Int,
    @SerialName("pages") val lastPage: Int,
    @SerialName("total") val totalResultCharacterCount: Int,
)

@Serializable
internal data class Character(
    @SerialName("_id") val id : String,
    val birth: String,
    val death: String,
    val gender: String,
    val hair: String,
    val height: String,
    val name: String,
    val race: String,
    val realm: String,
    val spouse: String,
)