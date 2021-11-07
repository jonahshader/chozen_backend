package chozen.systems.networking

import org.java_websocket.WebSocket
import java.util.concurrent.ArrayBlockingQueue

class CustomWebSocket(private val socket: WebSocket) : IClient {
    private val messageQueue = ArrayBlockingQueue<String>(100)

    fun giveMessage(message: String) {
        messageQueue.put(message)
    }

    override fun getMessage(): String? {
        return messageQueue.poll()
    }

    override fun isClosed(): Boolean {
        return socket.isClosed
    }

    override fun sendMessage(message: String) {
        socket.send(message)
    }

    override fun close() {
        socket.close()
    }
}