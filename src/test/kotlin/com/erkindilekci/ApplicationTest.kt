package com.erkindilekci

import com.erkindilekci.models.ApiResponse
import com.erkindilekci.repository.HeroRepository
import com.erkindilekci.repository.HeroRepositoryImpl
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val repository: HeroRepository = HeroRepositoryImpl()

    @Test
    fun `access root endpoint, assert correct information`() = testApplication {
        client.get("/").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            assertEquals(
                expected = "Welcome to Boruto Book API!",
                actual = bodyAsText()
            )
        }
    }

    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() = testApplication {
        val pages = 1..5
        val heroes = listOf(
            repository.page1,
            repository.page2,
            repository.page3,
            repository.page4,
            repository.page5
        )
        pages.forEach { page ->
            client.get("/boruto/heroes?page=$page").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = status
                )

                val actual = Json.decodeFromString(ApiResponse.serializer(), string = bodyAsText())

                val expected = ApiResponse(
                    success = true,
                    message = "OK",
                    prevPage = if (page == 1) null else page - 1,
                    nextPage = if (page == 5) null else page + 1,
                    heroes = heroes[page - 1],
                    lastUpdated = actual.lastUpdated
                )

                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access all heroes endpoint, query non existing page number, assert error`() = testApplication {
        client.get("/boruto/heroes?page=6").apply {
            assertEquals(
                expected = HttpStatusCode.NoContent,
                actual = status
            )

            val actual = Json.decodeFromString(ApiResponse.serializer(), string = bodyAsText())

            val expected = ApiResponse(
                success = false,
                message = "Heroes not found!",
                lastUpdated = actual.lastUpdated
            )

            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access all heroes endpoint, query invalid page number, assert error`() = testApplication {
        client.get("/boruto/heroes?page=invalid").apply {
            assertEquals(
                expected = HttpStatusCode.BadRequest,
                actual = status
            )

            val actual = Json.decodeFromString(ApiResponse.serializer(), string = bodyAsText())

            val expected = ApiResponse(
                success = false,
                message = "Only numbers allowed!",
                lastUpdated = actual.lastUpdated
            )

            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access search heroes endpoint, query hero name, assert single hero result`() = testApplication {
        client.get("/boruto/heroes/search?name=sas").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )

            val actual = Json.decodeFromString(ApiResponse.serializer(), string = bodyAsText()).heroes.size

            assertEquals(
                expected = 1,
                actual = actual
            )
        }
    }

    @Test
    fun `access search heroes endpoint, query hero name, assert multiple heroes result`() = testApplication {
        client.get("/boruto/heroes/search?name=as").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )

            val actual = Json.decodeFromString(ApiResponse.serializer(), string = bodyAsText()).heroes.size

            assertEquals(
                expected = 3,
                actual = actual
            )
        }
    }

    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() = testApplication {
        client.get("/boruto/heroes/search?name=").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )

            val actual = Json.decodeFromString(ApiResponse.serializer(), string = bodyAsText()).heroes

            assertEquals(
                expected = emptyList(),
                actual = actual
            )
        }
    }

    @Test
    fun `access search heroes endpoint, query non existing hero, assert empty list as a result`() = testApplication {
        client.get("/boruto/heroes/search?name=unknown").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )

            val actual = Json.decodeFromString(ApiResponse.serializer(), string = bodyAsText()).heroes

            assertEquals(
                expected = emptyList(),
                actual = actual
            )
        }
    }

    @Test
    fun `access non existing endpoint, assert not found`() = testApplication {
        client.get("/unknown").apply {
            assertEquals(
                expected = HttpStatusCode.NotFound,
                actual = status
            )

            assertEquals(
                expected = "404: Page Not Found",
                actual = bodyAsText()
            )
        }
    }
}
