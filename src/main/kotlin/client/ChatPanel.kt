package client

import Message
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.EtchedBorder
import javax.swing.border.TitledBorder

class ChatPanel : JPanel() {
    private val chatArea: JPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
        background = Color.WHITE
        repaint()
    }
    private val scroll: JScrollPane = JScrollPane(chatArea)
    private var prevMaxScroll = 0;

    init {
        border = TitledBorder(EtchedBorder(), "Chat")
        scroll.verticalScrollBar
            .addAdjustmentListener { e ->
                if (e.adjustable.maximum != prevMaxScroll) {
                    prevMaxScroll = e.adjustable.maximum
                    e.adjustable.value = e.adjustable.maximum
                }
            }
        scroll.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(scroll)
        spacer()

    }

    fun spacer() {
        chatArea.add(Box.createRigidArea(Dimension(0, 5)))
    }

    fun addMessage(message: Message) {
        chatArea.add(JMessage(message))
        spacer()
        chatArea.revalidate()
        chatArea.repaint()
        println("Server sent: $message")
    }

    fun clear() {
        chatArea.removeAll()
        chatArea.revalidate()
        chatArea.repaint()
    }

}