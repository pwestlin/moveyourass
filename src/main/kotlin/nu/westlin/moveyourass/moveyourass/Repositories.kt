package nu.westlin.moveyourass.moveyourass

import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.await
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.flow
import org.springframework.data.r2dbc.core.into

class UserRepository(private val client: DatabaseClient) {

    companion object {
        private val logger = LoggerFactory.getLogger(UserRepository::class.java)
    }


    fun all() = client.select().from("users").asType<User>().fetch().flow()

    suspend fun get(id: String) =
        client.execute("SELECT * FROM users WHERE id = :id").bind("id", id).asType<User>().fetch().awaitOneOrNull()

    private suspend fun deleteAll() {
        logger.info("Deleting all data from database")
        client.execute("DELETE FROM users").await()
    }

    suspend fun create(user: User) {
        logger.info("Creating user $user")
        client.insert().into<User>().table("users").using(user).await()
    }

    suspend fun init() {
        client.execute("CREATE TABLE IF NOT EXISTS users (id varchar PRIMARY KEY, firstname varchar, lastname varchar);").await()
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