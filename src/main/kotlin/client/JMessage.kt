package client

import Message
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class JMessage(message: Message) : JPanel() {
    companion object {
        private val headerFont: Font = Font("Arial", Font.BOLD, 14)
        private val bodyFont: Font = Font("Arial", Font.PLAIN, 14)
    }

    init {


        isOpaque = true
        background = Color(message.preferredColor.red, message.preferredColor.green, message.preferredColor.blue, 20)

        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(message.preferredColor),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )

        val grid = GridBagLayout()

        val gbc = GridBagConstraints()

        layout = grid



        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 0.5
        gbc.anchor = GridBagConstraints.WEST

        val senderLabel = JLabel(message.sender).apply {
            font = headerFont
            foreground = message.preferredColor
            background = Color.GREEN
        }
        add(senderLabel, gbc)


        gbc.gridx = 1
        gbc.gridy = 0
        gbc.weightx = 0.5
        gbc.anchor = GridBagConstraints.EAST

        val timeLabel = JLabel(message.getFormattedDateTime()).apply {
            font = headerFont
            foreground = message.preferredColor
        }
        add(timeLabel, gbc)


        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.VERTICAL
        gbc.gridx = 0
        gbc.gridy = 1
        gbc.weightx = 0.5
        gbc.gridwidth = 2
        gbc.anchor = GridBagConstraints.WEST

        val textLabel = JLabel(message.text).apply {
            font = bodyFont
        }
        add(textLabel, gbc)

        maximumSize = Dimension(maximumSize.width, preferredSize.height)

        repaint()
    }
}