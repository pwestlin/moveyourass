package nu.westlin.moveyourass.moveyourass

import kotlinx.coroutines.flow.toList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.application
import org.springframework.transaction.reactive.TransactionalOperator

// TODO petves: Yes, I know that I shouldn't use the repo for testing the repo...
internal class UserRepositoryTest {

    private val dataApp = application(WebApplicationType.NONE) {
        enable(dataConfig)
    }

    private lateinit var ctx: ConfigurableApplicationContext
    private lateinit var repository: UserRepository
    private lateinit var txOperator: TransactionalOperator

    @Suppress("unused")
    @BeforeAll
    private fun beforeAll() {
        ctx = dataApp.run()

        repository = ctx.getBean()
        txOperator = ctx.getBean()
    }

    @Suppress("unused")
    @AfterAll
    private fun afterAll() {
        ctx.close()
    }

    @Test
    fun `all users`() {
        executeAndRollBack(txOperator) {
            assertThat(repository.all().toList()).isEmpty()

            val user1 = User("foo", "Fo", "O").also { repository.create(it) }
            val user2 = User("foobar", "Foo", "Bar").also { repository.create(it) }

            assertThat(repository.all().toList()).containsExactlyInAnyOrder(user1, user2)
        }
    }

    @Test
    fun `create user`() {
        executeAndRollBack(txOperator) {
            val user = User("foo", "Fo", "O").also { repository.create(it) }
            assertThat(repository.get(user.id)).isEqualTo(user)
        }
    }

    @Test
    fun `get a user`() {
        executeAndRollBack(txOperator) {
            val user = User("foo", "Fo", "O").also { repository.create(it) }
            assertThat(repository.get(user.id)).isEqualTo(user)
        }
    }

    @Test
    fun `get a user that does not exist`() {
        executeAndRollBack(txOperator) {
            assertThat(repository.get("ghw4u67fuwv")).isNull()
        }
    }
}