import java.io.Serializable

data class Message(val sender: String, val text: String, val timestamp: Long): Serializable
