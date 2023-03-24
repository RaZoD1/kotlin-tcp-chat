package client

import java.awt.Color
import java.net.InetAddress

class ChatClientConfig private constructor(
    val port: Int,
    val host: InetAddress,
    val userName: String,
    val preferredColor: Color
) {

    data class Builder(
        var port: Int = 5000,
        var host: InetAddress = InetAddress.getByName("localhost"),
        var userName: String = "Anonymous",
        var preferredColor: Color = Color.BLACK

        ) {
        fun port(port: Int) = apply { this.port = port }
        fun host(host: InetAddress) = apply { this.host = host }
        fun userName(userName: String) = apply { this.userName = userName }
        fun preferredColor(preferredColor: Color) = apply { this.preferredColor = preferredColor }
        fun build() = ChatClientConfig(this.port, this.host , this.userName, this.preferredColor)
    }
}