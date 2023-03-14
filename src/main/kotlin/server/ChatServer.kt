package server

import Message
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

//TODO: stop crashing when client disconnects

fun main() {
    val serverConfig =  ChatServerConfig.Builder().port(8000).build();
    val server = ChatServer(serverConfig);

}
class ChatServer (private val config: ChatServerConfig){
    private val clients = mutableListOf<ChatClientHandler>();
    private val serverSocket: ServerSocket = ServerSocket(config.port);
    init {
        println(serverSocket.isBound)
        println(serverSocket)
        while(true){
            val client = serverSocket.accept();
            val clientHandler = ChatClientHandler(client);
            println("Client connected: ${client.inetAddress.hostAddress}")
            clients.add(clientHandler);
            Thread(clientHandler).start();
            broadcastMessage("Client connected: ${client.inetAddress.hostAddress}")
        }
    }

    fun broadcastMessage(text: String){
        broadcastMessage(Message("Server", text, System.currentTimeMillis()))
    }
    fun broadcastMessage(message: Message){
        clients.forEach { client -> client.sendMessage(message) }
    }

    inner class ChatClientHandler(private val client: Socket) : Runnable {
        private val streamIn: ObjectInputStream = ObjectInputStream(client.getInputStream())
        private val streamOut: ObjectOutputStream = ObjectOutputStream(client.getOutputStream())
        override fun run() {


            while (!client.isClosed) {
                try {
                    when(val nextObject: Any = streamIn.readObject()){
                        is Message -> {
                            println("Client said: ${nextObject}")
                            broadcastMessage(nextObject)
                        }
                        is String -> {
                            println("Client said: $nextObject")
                            broadcastMessage(nextObject)
                        }
                        else -> {
                            println("Unknown object type")
                        }
                    }
                } catch (e: SocketException){
                    println("Client disconnected")
                    this.disconnect()
                }
            }
            println("Client disconnected")
        }

        fun sendMessage(text: String){
            sendMessage(Message("Server", text, System.currentTimeMillis()))
        }
        fun sendMessage(message: Message){
            streamOut.writeObject(message)
        }

        fun disconnect() {
            streamOut.close()
            streamIn.close()
            client.close();
            clients.remove(this);
        }

    }
}