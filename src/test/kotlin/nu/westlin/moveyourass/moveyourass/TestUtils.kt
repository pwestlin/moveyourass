package nu.westlin.moveyourass.moveyourass

import kotlinx.coroutines.runBlocking
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

/**
 * Runs [block] and rolls back the database transaction by using [txOperator].
 */
fun executeAndRollBack(txOperator: TransactionalOperator, block: suspend () -> Unit) {
    runBlocking {
        txOperator.executeAndAwait {
            block()
            it.setRollbackOnly()
        }
    }
}
