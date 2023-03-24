package client

import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.border.EtchedBorder
import javax.swing.border.TitledBorder

class InputPanel(private val client: ChatApp) : JPanel() {
    private val inputField: JTextField = JTextField()

    init {
        border = TitledBorder(EtchedBorder(), "Input")
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
            client.chatClient?.sendMessage(text)
        }
    }
}