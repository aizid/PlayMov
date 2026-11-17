package com.aizid.playmov.remote.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MonoDataSourceTest {

    private fun createMockClient(jsonResponse: String, status: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = ByteReadChannel(jsonResponse),
                        status = status,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    isLenient = true
                })
            }
        }
    }

    @Test
    fun getGenreMovieList_returnsExpectedGenres() = runTest {
        val json = """
            {
                "genres": [
                    {"id": 28, "name": "Action"},
                    {"id": 12, "name": "Adventure"}
                ]
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = MonoDataSourceImpl(client)

        val response = dataSource.getGenreMovieList()
        assertNotNull(response)
        assertEquals(2, response.genres.size)
        assertEquals("Action", response.genres[0].name)
        assertEquals(28, response.genres[0].id)
    }

    @Test
    fun getMovieDiscover_returnsPaginatedMovies() = runTest {
        val json = """
            {
                "page": 1,
                "results": [
                    {
                        "id": 550,
                        "title": "Fight Club",
                        "overview": "An insomniac office worker...",
                        "vote_average": 8.4,
                        "vote_count": 26000,
                        "poster_path": "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
                        "backdrop_path": "/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
                        "genre_ids": [18, 53]
                    }
                ],
                "total_pages": 100,
                "total_results": 2000
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = MonoDataSourceImpl(client)

        val response = dataSource.getMovieDiscover(genreId = 18, page = 1)
        assertNotNull(response)
        assertEquals(1, response.page)
        assertEquals(1, response.results.size)
        assertEquals("Fight Club", response.results[0].title)
    }

    @Test
    fun getMovieDetails_returnsCompleteDetails() = runTest {
        val json = """
            {
                "id": 550,
                "title": "Fight Club",
                "tagline": "Mischief. Mayhem. Soap.",
                "overview": "A ticking-time-bomb insomniac...",
                "release_date": "1999-10-15",
                "runtime": 139,
                "vote_average": 8.433,
                "vote_count": 26280,
                "genres": [
                    {"id": 18, "name": "Drama"}
                ]
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = MonoDataSourceImpl(client)

        val response = dataSource.getMovieDetails(550)
        assertNotNull(response)
        assertEquals(550, response.id)
        assertEquals("Fight Club", response.title)
        assertEquals(139, response.runtime)
        assertEquals(1, response.genres.size)
    }

    @Test
    fun getMovieVideos_returnsOfficialTrailers() = runTest {
        val json = """
            {
                "id": 550,
                "results": [
                    {
                        "iso_639_1": "en",
                        "iso_3166_1": "US",
                        "name": "Official Trailer",
                        "key": "O1DTD_A2VDM",
                        "site": "YouTube",
                        "size": 1080,
                        "type": "Trailer",
                        "official": true,
                        "published_at": "2015-02-26T03:19:25.000Z",
                        "id": "54e150a092514107aa003a74"
                    }
                ]
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = MonoDataSourceImpl(client)

        val response = dataSource.getMovieVideos(550)
        assertNotNull(response)
        assertEquals(1, response.results.size)
        assertEquals("O1DTD_A2VDM", response.results[0].key)
        assertEquals("YouTube", response.results[0].site)
        assertEquals("Trailer", response.results[0].type)
    }

    @Test
    fun searchMulti_returnsMixedResults() = runTest {
        val json = """
            {
                "page": 1,
                "results": [
                    {
                        "id": 550,
                        "media_type": "movie",
                        "title": "Fight Club"
                    },
                    {
                        "id": 1399,
                        "media_type": "tv",
                        "name": "Game of Thrones"
                    }
                ],
                "total_pages": 1,
                "total_results": 2
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = MonoDataSourceImpl(client)

        val response = dataSource.searchMulti("Fight", 1)
        assertNotNull(response)
        assertEquals(2, response.results.size)
        assertEquals("movie", response.results[0].mediaType)
        assertEquals("tv", response.results[1].mediaType)
    }
}
