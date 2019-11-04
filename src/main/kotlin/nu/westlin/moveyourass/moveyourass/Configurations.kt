package nu.westlin.moveyourass.moveyourass

import io.r2dbc.h2.H2ConnectionConfiguration
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.fu.kofu.configuration
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

private fun transactionalOperator(txMgr: ReactiveTransactionManager): TransactionalOperator {
    return TransactionalOperator.create(txMgr)
}

private fun r2dbcTransactionManager(connectionFactory: ConnectionFactory): R2dbcTransactionManager {
    return R2dbcTransactionManager(connectionFactory)
}

private fun h2ConnectionConfiguration(): H2ConnectionConfiguration {
    // TODO petves: Read url/user/password from properties file
    return H2ConnectionConfiguration.builder().url("mem:test;DB_CLOSE_DELAY=-1").build()
}

private fun h2ConnectionFactory(h2ConnectionConfiguration: H2ConnectionConfiguration): H2ConnectionFactory {
    return H2ConnectionFactory(h2ConnectionConfiguration)
}

private fun databaseClient(h2ConnectionFactory: H2ConnectionFactory): DatabaseClient {
    return DatabaseClient.builder().connectionFactory(h2ConnectionFactory).build()
}


val dataConfig = configuration {
    beans {
        // I can't use the below DSL because it has no transaction configuration and I want that
        //r2dbcH2()
        bean { h2ConnectionConfiguration() }
        bean(::h2ConnectionFactory)
        bean(::r2dbcTransactionManager)
        bean(::transactionalOperator)
        bean(::databaseClient)

        bean<UserRepository>()
    }

    listener<ApplicationReadyEvent> {
        runBlocking {
            ref<UserRepository>().init()
        }
    }
}

val initDatabaseWithDataConfig = configuration {
    listener<ApplicationReadyEvent> {
        runBlocking {
            ref<UserRepository>().setupTestData()
        }
    }
}

val webConfig = configuration {
    // TODO petves: Impl
}