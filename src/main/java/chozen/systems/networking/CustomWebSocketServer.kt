package chozen.systems.networking

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap

class CustomWebSocketServer(address: InetSocketAddress) : IServer, WebSocketServer(address) {
    private val sockets = ConcurrentHashMap<WebSocket, CustomWebSocket>()
    private val newClients = ArrayBlockingQueue<IClient>(100)

    override fun acceptNewIClient(): IClient {
        return newClients.take()
    }

    override fun onOpen(conn: WebSocket, p1: ClientHandshake?) {
        val newSocket = CustomWebSocket(conn)
        newClients.put(newSocket)
        sockets[conn] = newSocket
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        sockets.remove(conn)
    }

    override fun onMessage(conn: WebSocket, message: String) {
        sockets[conn]!!.giveMessage(message)
    }

    override fun onError(conn: WebSocket, ex: Exception) {

    }

    override fun onStart() {
        println("websocket server started")
    }
}