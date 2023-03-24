package client

import Message
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.Socket
import java.net.SocketException

fun main(args: Array<String>) {

    val config = ChatClientConfig.Builder().port(8000).host(InetAddress.getByName("localhost")).build();
    val client = ChatClient(config);
}

class ChatClient(private val config: ChatClientConfig) {

    private val socket = Socket(config.host, config.port);

    private val streamOut: ObjectOutputStream = ObjectOutputStream(socket.getOutputStream());
    private val streamIn: ObjectInputStream = ObjectInputStream(socket.getInputStream());

    private val messageListeners = mutableListOf<(Message) -> Unit>();
    private val closeListeners = mutableListOf<() -> Unit>();

    init {
        Thread(this::listenForMessages).start();
    }

    private fun listenForMessages() {
        while (!socket.isClosed) {
            try {
                streamIn.readObject()?.let {
                    when (it) {
                        is Message -> {
                            messageListeners.forEach { listener -> listener(it) }
                        }
                        else -> {
                            println("Unknown object type")
                        }
                    }
                }
            } catch (e: SocketException) {
                disconnect()
                println("Socket closed, stopping listening")
                break;
            }
        }
    }


    fun sendMessage(message: Message) {
        if (!socket.isClosed) {
            streamOut.writeObject(message)
        }
    }

    fun sendMessage(text: String) {
        sendMessage(Message(config.userName, text, System.currentTimeMillis(), config.preferredColor))
    }

    fun addMessageListener(listener: (Message) -> Unit) {
        messageListeners.add(listener);
    }
    fun addCloseListener(listener: () -> Unit) {
        closeListeners.add(listener);
    }

    fun disconnect() {
        closeListeners.forEach { listener -> listener() }
        try {
            streamOut.close()
            streamIn.close()
        } catch (e: Exception) {
            println("Error closing streams")
        }
        socket.close();
    }

}