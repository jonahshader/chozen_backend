package chozen.systems

import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class ServerManager(port: Int) {
    private val serverSocket = ServerSocket(port)
    private val connectedUsers = mutableListOf<Socket>()

    init {
        thread {
            while (true) {
                connectedUsers += serverSocket.accept()
            }
        }


    }
}