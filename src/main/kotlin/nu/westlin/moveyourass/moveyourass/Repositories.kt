package nu.westlin.moveyourass.moveyourass

import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.await
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.flow
import org.springframework.data.r2dbc.core.into
import java.time.LocalDateTime

class UserRepository(private val client: DatabaseClient) {

    companion object {
        private val logger = LoggerFactory.getLogger(UserRepository::class.java)
    }


    fun all() = client.select().from("users").asType<User>().fetch().flow()

    suspend fun get(id: String) =
        client.execute("SELECT * FROM users WHERE id = :id").bind("id", id).asType<User>().fetch().awaitOneOrNull()

    private suspend fun deleteAll() {
        logger.info("Deleting all data from users")
        client.execute("DELETE FROM users").await()
    }

    suspend fun create(user: User) {
        logger.info("Creating user $user")
        client.insert().into<User>().table("users").using(user).await()
    }

    suspend fun init() {
        client.execute("CREATE TABLE IF NOT EXISTS users (id varchar PRIMARY KEY, firstname varchar not null, lastname varchar not null);").await()
    }

    /**
     * For sample!
     */
    suspend fun setupTestData() {
        deleteAll()
        create(User("peterw", "Peter", "Westlin"))
        create(User("camillal", "Camilla", "Löfling"))
    }

}

class TrainingSessionRepository(private val client: DatabaseClient) {

    companion object {
        private val logger = LoggerFactory.getLogger(TrainingSessionRepository::class.java)
    }

    fun allByUserId(userId: String) =
        client.execute("select * from trainingSessions where user_id = :user_id order by date_time desc").bind("user_id", userId)
            .asType<TrainingSession>().fetch().flow()

    suspend fun init() {
        // TODO petves: PK and FK?
        // val userId: String, val dateTime: LocalDateTime, val form: Form, val duration: Int, val comment: String? = null
        client.execute("CREATE TABLE IF NOT EXISTS trainingSessions (id IDENTITY NOT NULL PRIMARY KEY, user_id varchar not null, date_time datetime not null, form varchar not null, duration number not null, comment varchar, foreign key (user_id) references users(id));").await()
    }

    private suspend fun deleteAll() {
        logger.info("Deleting all data from trainingSessions")
        client.execute("DELETE FROM trainingSessions").await()
    }

    suspend fun create(trainingSession: TrainingSession) {
        logger.info("Creating trainingSession $trainingSession")
        client.insert().into<TrainingSession>().table("trainingSessions").using(trainingSession).await()
    }

    /**
     * For sample!
     */
    suspend fun setupTestData() {
        deleteAll()

        val peterw = User("peterw", "Peter", "Westlin")
        create(TrainingSession(peterw.id, LocalDateTime.now().minusDays(3), TrainingSession.Form.DOG_WALK, 30))
        create(TrainingSession(peterw.id, LocalDateTime.now(), TrainingSession.Form.MOUNTANBIKE, 90))
        create(TrainingSession(peterw.id, LocalDateTime.now().plusDays(3).plusHours(1), TrainingSession.Form.DOG_WALK, 45, "Pee and poo"))
    }
}