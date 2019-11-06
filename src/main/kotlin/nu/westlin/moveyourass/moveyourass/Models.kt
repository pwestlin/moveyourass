package nu.westlin.moveyourass.moveyourass

import java.time.LocalDateTime

data class User(val id: String, val firstname: String, val lastname: String)

data class TrainingSession(val userId: String, val dateTime: LocalDateTime, val form: Form, val duration: Int, val comment: String? = null) {
    enum class Form {
        MOUNTANBIKE, SWINNING, DOG_WALK, POWER_WALK
    }
}

