import java.awt.Color
import java.io.Serializable
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Message(val sender: String, val text: String, val timestamp: Long, val preferredColor: Color): Serializable {

    fun getDateTime(): LocalDateTime =
        LocalDateTime.ofEpochSecond(timestamp / 1000, 0, OffsetDateTime.now().offset)

    fun getFormattedDateTime(): String =
        getDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss d MMM uuuu"))

    override fun toString(): String {
        return "$preferredColor ${getFormattedDateTime()} $sender: $text"
    }
}
