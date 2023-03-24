package client

import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EtchedBorder
import javax.swing.border.TitledBorder

class StatusPanel : JPanel() {
    private val statusField: JLabel = JLabel("Disconnected")

    init {
        statusField.font = statusField.font.deriveFont(Font.BOLD)
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        border = TitledBorder(EtchedBorder(), "Status")
        val statusLabel = JLabel("Status: ")
        add(statusLabel)
        add(statusField)
    }

    fun setStatus(status: String) {
        statusField.text = status
    }
}