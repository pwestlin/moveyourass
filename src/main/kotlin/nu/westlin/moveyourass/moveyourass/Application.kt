package nu.westlin.moveyourass.moveyourass

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application

val app = application(WebApplicationType.REACTIVE) {
    enable(loggingConfig)
    enable(dataConfig)
    enable(initDatabaseWithDataConfig)
    enable(webConfig)
}

fun main() {
    app.run()
}
