package client

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.*


fun main(args: Array<String>) {
/*    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (ex: Exception) {
        ex.printStackTrace()
    }*/
    Locale.setDefault(Locale.ENGLISH);
    ResourceBundle.clearCache();
    SwingUtilities.invokeLater { ChatApp() }
}

class ChatApp : JFrame("The Ultimate Chat App") {

     val content: Content = Content(this)
    var chatClient: ChatClient? = null
        set(value) {
            field?.disconnect()
            field = value
            value?.addMessageListener { message -> content.chatPanel.addMessage(message) }
            value?.addCloseListener { field = null; onClientClose() }
        }



    init {
        setSize(800, 800)
        defaultCloseOperation = EXIT_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                chatClient?.disconnect()
            }
        })

        contentPane = content
        isLocationByPlatform = true
        isVisible = true
        println("ChatApp started")
    }

    private fun onClientClose(){
        content.statusPanel.setStatus("Disconnected")
        content.chatPanel.clear()
        JOptionPane.showMessageDialog(this, "Connection closed", "Connection closed", JOptionPane.INFORMATION_MESSAGE)
    }

}
