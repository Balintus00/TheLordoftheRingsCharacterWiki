package hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data

import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.BuildKonfig
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.model.Character
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.data.model.CharacterListPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Birth
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterNameFilter
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.CharacterPage
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Death
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Gender
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Hair
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Height
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Id
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Name
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.PageSpecification
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Race
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Realm
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Spouse
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.repository.datasource.RemoteCharacterDatasource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import hu.bme.aut.ixnoyb.thelordoftheringscharacterwiki.domain.Character as DomainCharacter

internal class KtorRemoteCharacterDatasource(
    private val httpClient: HttpClient,
) : RemoteCharacterDatasource {

    override suspend fun getById(id: Id): DomainCharacter = withContext(Dispatchers.IO) {
        require(id.value.isBlank().not()) { ERROR_MESSAGE_INVALID_CHARACTER_ID }

        val response = httpClient.get(BASE_URL) {
            headers {
                bearerAuth(BuildKonfig.THE_ONE_API_KEY)
            }
            url {
                appendPathSegments(listOf(PATH_SEGMENT_CHARACTER, id.value))
            }
        }

        if (response.status.value in 200..299) {
            val responseBody = response.body<CharacterListPage>()
            responseBody.characters.first().toDomainCharacter()
        } else {
            throw IllegalStateException(ERROR_MESSAGE_NOT_SUCCESSFUL_RESPONSE_CODE)
        }
    }

    private fun Character.toDomainCharacter(): DomainCharacter = DomainCharacter(
        id = Id(id),
        birth = Birth(birth),
        death = Death(death),
        gender = Gender(gender),
        hair = Hair(hair),
        height = Height(height),
        name = Name(name),
        race = Race(race),
        realm = Realm(realm),
        spouse = Spouse(spouse),
    )

    override suspend fun getPage(
        nameFilter: CharacterNameFilter?,
        page: PageSpecification,
    ): CharacterPage = withContext(Dispatchers.IO) {
        val response = httpClient.get(BASE_URL) {
            headers {
                bearerAuth(BuildKonfig.THE_ONE_API_KEY)
            }
            url {
                appendPathSegments(PATH_SEGMENT_CHARACTER)

                parameters.append(QUERY_PARAMETER_KEY_SORT, QUERY_PARAMETER_VALUE_NAME_ASCENDING)
                parameters.append(QUERY_PARAMETER_KEY_LIMIT, page.size.value.toString())
                parameters.append(QUERY_PARAMETER_KEY_PAGE, page.number.value.toString())
                if (nameFilter != null) {
                    parameters.append(QUERY_PARAMETER_KEY_NAME, nameFilter.value)
                }
            }
        }

        if (response.status.value in 200..299) {
            val characterListPage = response.body<CharacterListPage>()
            CharacterPage(
                characters = characterListPage.characters.map { it.toDomainCharacter() },
                isNextPageExist = characterListPage.run { currentPage < lastPage },
            )
        } else {
            throw IllegalStateException(ERROR_MESSAGE_NOT_SUCCESSFUL_RESPONSE_CODE)
        }
    }

    companion object {
        const val ERROR_MESSAGE_INVALID_CHARACTER_ID = "ERROR_MESSAGE_INVALID_CHARACTER_ID"
        const val ERROR_MESSAGE_NOT_SUCCESSFUL_RESPONSE_CODE =
            "ERROR_MESSAGE_NOT_SUCCESSFUL_RESPONSE_CODE"

        private const val BASE_URL = "https://the-one-api.dev/v2"

        private const val PATH_SEGMENT_CHARACTER = "character"

        private const val QUERY_PARAMETER_KEY_LIMIT = "limit"
        private const val QUERY_PARAMETER_KEY_NAME = "name"
        private const val QUERY_PARAMETER_KEY_PAGE = "page"
        private const val QUERY_PARAMETER_KEY_SORT = "sort"

        private const val QUERY_PARAMETER_VALUE_NAME_ASCENDING = "name:asc"
    }
}