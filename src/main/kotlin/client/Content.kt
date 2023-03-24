package client

import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.EtchedBorder

class Content(private val chatApp: ChatApp) : JPanel() {
    val chatPanel: ChatPanel = ChatPanel()
    val inputPanel: InputPanel = InputPanel(chatApp)
    val controlPanel: ControlPanel = ControlPanel(chatApp)
    val statusPanel: StatusPanel = StatusPanel()

    init {
        layout = BorderLayout()
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        add(chatPanel, BorderLayout.CENTER)
        add(inputPanel, BorderLayout.SOUTH)

        val controlPanelContainer = JPanel()
        controlPanelContainer.layout = GridLayout(2, 1).also { it.hgap = 10 }
        controlPanelContainer.border = EtchedBorder()

        controlPanelContainer.add(controlPanel)
        controlPanelContainer.add(statusPanel)
        add(controlPanelContainer, BorderLayout.NORTH)

    }
}