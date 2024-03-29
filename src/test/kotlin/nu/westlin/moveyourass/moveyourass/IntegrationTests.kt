package nu.westlin.moveyourass.moveyourass

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

internal class IntegrationTests {
    // TODO petves: Impl more test

    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()

    private lateinit var context: ConfigurableApplicationContext

    @Suppress("unused")
    @BeforeAll
    fun beforeAll() {
        context = app.run()
    }

    @Nested
    @DisplayName("API")
    inner class ApiTest {

        @Test
        fun `all users`() {
            client.get().uri("/api/users").accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList<User>().contains(
                    User("peterw", "Peter", "Westlin"),
                    User("camillal", "Camilla", "Löfling")
                )
        }

        @Test
        fun `all sessions by user id`() {
            val users: List<TrainingSession>? = client.get().uri("/api/sessions/user/peterw").accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList<TrainingSession>().returnResult().responseBody

            assertThat(users)
                .hasSize(3)
                .allMatch { it.userId == "peterw" }
        }
    }

    @Nested
    @DisplayName("view")
    inner class ViewTest {

        @Test
        fun `all users`() {
            client.get().uri("/").accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType("${MediaType.TEXT_HTML};charset=UTF-8")
                .expectBody().xpath("/html/head/title").isEqualTo("Move your ass!")
        }
    }

    @Suppress("unused")
    @AfterAll
    fun afterAll() {
        context.close()
    }

}