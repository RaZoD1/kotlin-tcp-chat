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

class ChatClient (private val config: ChatClientConfig){

    private val socket = Socket(config.host, config.port);
    private val streamOut: ObjectOutputStream = ObjectOutputStream(socket.getOutputStream());
    private val streamIn: ObjectInputStream = ObjectInputStream(socket.getInputStream());

    private val messageListeners = mutableListOf<(Message) -> Unit>();

    init {
        Thread(this::listenForMessages).start();
    }

    private fun listenForMessages(){
        while (!socket.isClosed){
            try {
                streamIn.readObject()?.let {
                    when(it){
                        is Message -> {
                            messageListeners.forEach { listener -> listener(it) }
                        }
                        is String -> {
                            messageListeners.forEach { listener -> listener(Message("Unknown", it, System.currentTimeMillis())) }
                        }
                        else -> {
                            println("Unknown object type")
                        }
                    }
                }
            } catch (e: SocketException){
                println("Socket closed, stopping listening")
                break;
            }
        }
    }

    fun sendMessage(message: String){
        sendMessage(Message(config.userName, message, System.currentTimeMillis()))
    }
    fun sendMessage(message: Message){
        if(!socket.isClosed){
            streamOut.writeObject(message)
        }
    }

    fun addMessageListener(listener: (Message) -> Unit){
        messageListeners.add(listener);
    }

    fun disconnect(){
        sendMessage(Message(config.userName, "IS LEAVING", System.currentTimeMillis()))

        streamOut.close()
        streamIn.close()
        socket.close();

    }

}