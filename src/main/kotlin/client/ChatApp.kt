package client

import Message
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.net.InetAddress
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.swing.*


//TODO: make the chat app look nice
//TODO: make the chat panel scrollable

fun main(args: Array<String>) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    SwingUtilities.invokeLater { ChatApp() }
}

class ChatApp : JFrame("The Ultimate Chat App") {

    private val content: Content = Content()
    private var chatClient: ChatClient? = null
        set(value) {
            field?.disconnect()
            field = value
            value?.addMessageListener { message -> content.chatPanel.addMessage(message) }
        }

    init {
        title = "ChatApp"
        setSize(500, 500)
        defaultCloseOperation = EXIT_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                chatClient?.disconnect()
            }
        })

        contentPane = content
        isVisible = true
        println("ChatApp started")
    }

    inner class Content : JPanel() {
        val chatPanel: ChatPanel = ChatPanel()
        val inputPanel: InputPanel = InputPanel()
        val controlPanel: ControlPanel = ControlPanel()
        val statusPanel: StatusPanel = StatusPanel()

        init {
            layout = BorderLayout()
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

            add(chatPanel, BorderLayout.CENTER)
            add(inputPanel, BorderLayout.SOUTH)

            val controlPanelContainer = JPanel()
            controlPanelContainer.layout = GridLayout(2, 1).also { it.hgap = 10 }

            controlPanelContainer.add(controlPanel)
            controlPanelContainer.add(statusPanel)
            add(controlPanelContainer, BorderLayout.NORTH)

        }
    }

    inner class StatusPanel : JPanel() {
        private val statusField: JLabel = JLabel("Disconnected")

        init {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            val statusLabel = JLabel("Status: ")
            add(statusLabel)
            add(statusField)
        }

        fun setStatus(status: String) {
            statusField.text = status
        }
    }

    inner class ControlPanel : JPanel() {
        private val portModel = SpinnerNumberModel(8000, 0, 65535, 1);
        private val hostField = JTextField("");
        private val userNameField = JTextField("")

        init {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            val connectButton = JButton("Connect")
            connectButton.addActionListener { onConnectClick() }
            val disconnectButton = JButton("Disconnect")
            disconnectButton.addActionListener { onDisconnectClick() }
            val portField = JSpinner(portModel)

            add(JLabel("Username: "))
            add(userNameField)
            add(JLabel("Host: "))
            add(hostField)
            add(JLabel("Port: "))
            add(portField)
            add(connectButton)
            add(disconnectButton)
        }

        fun getPort(): Int {
            return portModel.number.toInt();
        }

        fun getHost(): String {
            return hostField.text;
        }
        fun getUsername(): String {
            return userNameField.text;
        }

        private fun onConnectClick() {
            val config = ChatClientConfig.Builder()
                .port(getPort())
                .host(InetAddress.getByName(getHost()))
                .userName(getUsername())
                .build();
            chatClient = ChatClient(config);
            content.statusPanel.setStatus("Connected to ${getHost()}:${getPort()} as ${config.userName}")
        }

        private fun onDisconnectClick() {
            chatClient?.disconnect();
            content.statusPanel.setStatus("Disconnected")
        }

    }

    inner class ChatPanel : JPanel() {
        val chatArea: JTextArea = JTextArea()

        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            chatArea.isEditable = false
            this@ChatPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(chatArea)

        }

        fun addMessage(message: Message) {
            val dateTime = LocalDateTime.ofEpochSecond(message.timestamp/1000, 0, OffsetDateTime.now().offset)
            val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("[HH:mm:ss]"))

            chatArea.append("$formattedDateTime ${message.sender}: ${message.text} ${System.lineSeparator()}")
            println("Server sent: $message")
        }

    }

    inner class InputPanel : JPanel() {
        private val inputField: JTextField = JTextField()
        init {
            layout = BorderLayout(1, 1)
            inputField.addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        onEnterPressed(inputField)
                    }
                }
            })

            add(inputField, BorderLayout.CENTER)

        }

        fun onEnterPressed(textField: JTextField) {
            val text: String = textField.text.trim()
            textField.text = ""
            if (text.isNotEmpty()) {
                println(text)
                chatClient?.sendMessage(text)
            }
        }
    }

}
