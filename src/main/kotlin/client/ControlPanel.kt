package client

import java.awt.Color
import java.awt.Dimension
import java.net.InetAddress
import java.util.*
import javax.swing.*
import javax.swing.border.EtchedBorder
import javax.swing.border.TitledBorder

class ControlPanel(private val client: ChatApp) : JPanel() {
    private val portModel = SpinnerNumberModel(8000, 0, 65535, 1);
    private val hostField = JTextField("")
    private val userNameField = JTextField("")
    private val colorPickerButton = JButton("Pick color")
    private val colorShowBox = JPanel()
    private var preferredColor: Color
        get() = colorShowBox.background
        set(value) {
            colorShowBox.background = value
            colorShowBox.repaint()
        }

    init {
        border = TitledBorder(EtchedBorder(), "Connection")
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        val connectButton = JButton("Connect")
        connectButton.addActionListener { onConnectClick() }
        val disconnectButton = JButton("Disconnect")
        disconnectButton.addActionListener { onDisconnectClick() }
        val portField = JSpinner(portModel)

        colorShowBox.border  = EtchedBorder()
        colorShowBox.size = Dimension(20, 20)
        colorShowBox.minimumSize = Dimension(20, 20)
        colorShowBox.maximumSize = Dimension(20, 20)
        colorShowBox.background = Color.BLACK


        colorPickerButton.addActionListener {
            val chooser = JColorChooser();
            chooser.previewPanel = JPanel();
            chooser.locale = Locale.ENGLISH;
            chooser.color = preferredColor;
            val dialog = JColorChooser.createDialog(
                client, "Pick a color", true, chooser,
                {
                    val color = chooser.color
                    preferredColor = color
                },
                null,
            )
            dialog.show()
        }




        add(colorPickerButton)
        add(colorShowBox)
        add(JLabel("Username: "))
        add(userNameField)
        add(JLabel("Host: "))
        add(hostField)
        add(JLabel("Port: "))
        add(portField)
        add(connectButton)
        add(disconnectButton)

        colorShowBox.size.also { println(it) }
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
            .preferredColor(preferredColor)
            .build();
        try {
            client.chatClient = ChatClient(config);
            client.content.statusPanel.setStatus("Connected to ${getHost()}:${getPort()} as ${config.userName}")

        } catch (e: Exception) {
            client.content.statusPanel.setStatus(e.message ?: "Error")
            JOptionPane.showMessageDialog(client, e.message, "Error", JOptionPane.ERROR_MESSAGE)
            return;
        }
    }

    private fun onDisconnectClick() {
        client.chatClient?.disconnect();
        client.content.statusPanel.setStatus("Disconnected")
    }

}