package nu.westlin.moveyourass.moveyourass

import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient

internal class ViewHandlerTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var client: WebTestClient

    private val user1 = User("user1", "User", "1")
    private val user2 = User("user2", "User", "2")

    @Suppress("unused")
    @BeforeAll
    private fun init() {
        client = WebTestClient.bindToRouterFunction(viewRoutes(ViewHandler(userRepository))).build()
    }

    @Test
    fun `How to test models?`() {
        // TODO petves: How to test?
    }
}