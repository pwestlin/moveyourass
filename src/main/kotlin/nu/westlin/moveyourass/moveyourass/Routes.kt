package nu.westlin.moveyourass.moveyourass

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

fun viewRoutes(viewHandler: ViewHandler) = coRouter {
    GET("/", viewHandler::allUsersView)
    GET("/index.html", viewHandler::allUsersView)
    GET("/sessions/user/{id}", viewHandler::allSessionsForUserView)
}

fun apiRoutes(apiHandler: ApiHandler) = coRouter {
    accept(MediaType.APPLICATION_JSON).nest {
        "/api".nest {
            "/users".nest {
                GET("", apiHandler::allUsers)
                GET("/{id}", apiHandler::byId)
            }
            "/sessions/user/{id}".nest {
                GET("", apiHandler::allSessionsByUserId)
            }
        }
    }
}