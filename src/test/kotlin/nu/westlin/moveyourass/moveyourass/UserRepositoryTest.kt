package nu.westlin.moveyourass.moveyourass

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.application
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

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
        println("ctx.beanDefinitionNames = \n" + ctx.beanDefinitionNames.joinToString("\n"))
        repository = ctx.getBean()
        txOperator = ctx.getBean()
    }

    @Suppress("unused")
    @AfterAll
    private fun afterAll() {
        ctx.close()
    }

    @Test
    fun `get all`() {
        executeAndRollBack {
            assertThat(repository.all().toList()).isEmpty()

            val user1 = User("foo", "Fo", "O").also { repository.create(it) }
            val user2 = User("foobar", "Foo", "Bar").also { repository.create(it) }

            assertThat(repository.all().toList()).containsExactlyInAnyOrder(user1, user2)
        }
    }

    @Test
    fun `get all2`() {
        executeAndRollBack {
            assertThat(repository.all().toList()).isEmpty()

            val user1 = User("foo", "Fo", "O").also { repository.create(it) }
            val user2 = User("foobar", "Foo", "Bar").also { repository.create(it) }

            assertThat(repository.all().toList()).containsExactlyInAnyOrder(user1, user2)
        }
    }

    // TODO petves: Move to top-level function?
    /**
     * Runs [block] and rolls back the database transaction.
     */
    private fun executeAndRollBack(block: suspend () -> Unit) {
        runBlocking {
            txOperator.executeAndAwait {
                block()
                it.setRollbackOnly()
            }
        }
    }
}