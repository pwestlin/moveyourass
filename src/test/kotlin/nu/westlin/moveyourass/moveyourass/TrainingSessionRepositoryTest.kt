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
import java.time.LocalDateTime

// TODO petves: Yes, I know that I shouldn't use the repo for testing the repo...
internal class TrainingSessionRepositoryTest {

    private val dataApp = application(WebApplicationType.NONE) {
        enable(dataConfig)
    }

    private lateinit var ctx: ConfigurableApplicationContext
    private lateinit var repository: TrainingSessionRepository
    private lateinit var txOperator: TransactionalOperator

    private val user = User("foo", "Fo", "o")
    private val fooSession1 = TrainingSession(user.id, LocalDateTime.now().minusDays(3), TrainingSession.Form.DOG_WALK, 45)
    private val fooSession2 = TrainingSession(user.id, LocalDateTime.now().minusHours(3), TrainingSession.Form.MOUNTANBIKE, 90)

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
    fun `all by userId`() {
        executeAndRollBack(txOperator) {
            createUser(user)

            assertThat(repository.allByUserId(user.id).toList()).isEmpty()
            repository.create(fooSession1)
            repository.create(fooSession2)
            val fooSession3 = fooSession2.copy(dateTime = fooSession2.dateTime.minusYears(47))
                .also { repository.create(it) }
            val fooSession4 = fooSession2.copy(dateTime = fooSession2.dateTime.plusYears(47))
                .also { repository.create(it) }

            val sessions = repository.allByUserId(user.id).toList()
            assertThat(sessions).containsExactly(fooSession4, fooSession2, fooSession1, fooSession3)
        }
    }

    @Test
    fun `create session`() {
        executeAndRollBack(txOperator) {
            createUser(user)

            repository.create(fooSession1)
            assertThat(repository.allByUserId(fooSession1.userId).toList()).containsExactly(fooSession1)
        }
    }

    private suspend fun createUser(user: User) {
        ctx.getBean<UserRepository>().create(user)
    }
}