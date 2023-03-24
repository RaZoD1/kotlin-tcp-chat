package server

class ChatServerConfig private constructor(
    val port: Int,
) {

    data class Builder(
        var port: Int = 5000,
    ) {
        fun port(port: Int) = apply { this.port = port }
        fun build() = ChatServerConfig(this.port)
    }

    override fun toString(): String {
        return "ChatServerConfig(port=$port)"
    }

}