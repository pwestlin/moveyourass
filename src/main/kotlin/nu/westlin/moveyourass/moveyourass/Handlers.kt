package nu.westlin.moveyourass.moveyourass

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.renderAndAwait

@Suppress("UNUSED_PARAMETER")
class ViewHandler(private val userRepository: UserRepository) {

    suspend fun allUsersView(request: ServerRequest): ServerResponse {
        return ok().renderAndAwait("users", mapOf("users" to userRepository.all()))
    }
}

@Suppress("UNUSED_PARAMETER")
class ApiHandler(private val userRepository: UserRepository) {

    suspend fun allUsers(request: ServerRequest): ServerResponse {
        return ok().bodyAndAwait(userRepository.all())
    }

    suspend fun byId(request: ServerRequest): ServerResponse {
        return when (val user = userRepository.get(request.pathVariable("id"))) {
            null -> notFound().buildAndAwait()
            else -> ok().bodyValueAndAwait(user)
        }
    }
}