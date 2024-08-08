package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.remote.dto

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