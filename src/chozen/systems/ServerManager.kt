package chozen.systems

import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class ServerManager(port: Int) {
    private val serverSocket = ServerSocket(port)
    private val connectedUsersLock = ReentrantLock()
    private val connectedUsers = mutableListOf<User>()

    init {
        // run a thread for gathering new user connections
        thread {
            while (true) {
                val newUser = serverSocket.accept()
                connectedUsersLock.lock()
                connectedUsers += User(newUser)
                connectedUsersLock.unlock()
                Thread.sleep(5) // might not be necessary
            }
        }

        // continuously poll inputs from the users
        while (true) {
            connectedUsersLock.lock()
            connectedUsers.forEach { it.pollInput(this) }
            connectedUsersLock.unlock()
            Thread.sleep(5) // might not be necessary idk lol
        }
    }
}