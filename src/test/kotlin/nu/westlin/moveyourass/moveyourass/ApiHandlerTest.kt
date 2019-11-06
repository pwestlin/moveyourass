package nu.westlin.moveyourass.moveyourass

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.time.LocalDateTime

internal class ApiHandlerTest {

    private val userRepository = mockk<UserRepository>()
    private val sessionRepository = mockk<TrainingSessionRepository>()
    private lateinit var client: WebTestClient

    private val user1 = User("user1", "User", "1")
    private val user1Session1 = TrainingSession(user1.id, LocalDateTime.now().minusDays(3), TrainingSession.Form.DOG_WALK, 45)
    private val user1Session2 = TrainingSession(user1.id, LocalDateTime.now().minusHours(3), TrainingSession.Form.MOUNTANBIKE, 90)

    private val user2 = User("user2", "User", "2")

    @Suppress("unused")
    @BeforeAll
    private fun init() {
        client = WebTestClient.bindToRouterFunction(apiRoutes(ApiHandler(userRepository, sessionRepository))).build()
    }

    @Test
    fun `all users`() {
        runBlocking {
            coEvery { userRepository.all() } returns listOf(user1, user2).asFlow()

            client.get().uri("/api/users").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList<User>().contains(user1, user2)
        }
    }

    @Test
    fun `one user by id`() {
        runBlocking {
            val user = user2
            coEvery { userRepository.get(user.id) } returns user

            client.get().uri("/api/users/{id}", user.id).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody<User>().isEqualTo(user)
        }
    }

    @Test
    fun `one user by id that is not found`() {
        runBlocking {
            val user = user2
            coEvery { userRepository.get(user.id) } returns null

            client.get().uri("/api/users/{id}", user.id).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectHeader().doesNotExist("Accept")
                .expectBody().isEmpty
        }
    }

    @Test
    fun `all sessions by userId`() {
        runBlocking {
            coEvery { sessionRepository.allByUserId(user1.id) } returns listOf(user1Session1, user1Session2).asFlow()

            client.get().uri("/api/sessions/user/{id}", user1.id).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList<TrainingSession>().contains(user1Session1, user1Session2)
        }
    }
}