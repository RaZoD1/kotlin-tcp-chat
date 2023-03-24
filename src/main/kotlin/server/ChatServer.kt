package server

import Message
import java.awt.Color
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*


fun main(args: Array<String>) {
    val serverConfig = parseArgs(args)
    val server = ChatServer(serverConfig);
    server.startListening()
}

fun parseArgs(args: Array<String>): ChatServerConfig {
    val builder = ChatServerConfig.Builder()

    var i = 0;
    while (i < args.size) {
        when (args[i]) {
            "-p", "--port" -> {
                builder.port(args[++i].toInt())
            }
        }
        i++;
    }
    return builder.build()
}

class ChatServer(private val config: ChatServerConfig) : Runnable {
    companion object {
        val SERVER_COLOR: Color = Color.BLACK
    }

    private val clients = Collections.synchronizedList(mutableListOf<ChatClientHandler>())
    private val serverSocket: ServerSocket = ServerSocket(config.port)

    init {
        println(config)
    }

    fun startListening() {
        Thread(this).start()
    }

    override fun run() {
        while (!serverSocket.isClosed) {
            try {
                val client = serverSocket.accept()
                val clientHandler = ChatClientHandler(client)
                println("Client connected: ${client.inetAddress.hostAddress}")
                clients.add(clientHandler)
                Thread(clientHandler).start()
                broadcastServerMessage("Client connected: ${client.inetAddress.hostAddress}")
            } catch (e: IOException) {
                println("Server closed")
                break
            }
        }
    }

    private fun broadcastServerMessage(text: String) {
        broadcastMessage(Message("Server", text, System.currentTimeMillis(), SERVER_COLOR))
    }

    fun broadcastMessage(message: Message) {
        clients.forEach { client -> client.sendMessage(message) }
    }

    inner class ChatClientHandler(private val client: Socket) : Runnable {
        private val streamIn: ObjectInputStream = ObjectInputStream(client.getInputStream())
        private val streamOut: ObjectOutputStream = ObjectOutputStream(client.getOutputStream())
        override fun run() {
            while (!client.isClosed && client.isConnected) {
                try {
                    when (val nextObject: Any = streamIn.readObject()) {
                        is Message -> {
                            receiveMessage(nextObject)
                        }
                        else -> {
                            println("Unknown object type")
                        }
                    }
                } catch (e: IOException) {
                    this.onSocketClosed()
                } catch (e: EOFException) {
                    this.onSocketClosed()
                }
            }
        }

        private fun onSocketClosed() {
            println("Client disconnected")
            this.close()
        }

        private fun receiveMessage(message: Message) {
            println("Client said: $message")
            broadcastMessage(message)
        }

        fun sendMessage(message: Message) {
            streamOut.writeObject(message)
        }


        fun close() {
            streamOut.close()
            streamIn.close()
            client.close();
            clients.remove(this);
        }

    }


}
