package chozen.systems

import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class User(socket: Socket) {
    private val inputStreamReader = InputStreamReader(socket.getInputStream())
    private val outputWriter = PrintWriter(socket.getOutputStream(), true)

    fun pollInput(serverManager: ServerManager) {
        inputStreamReader.forEachLine {
            var parts = it.split(' ')
            when (parts[0]) {
                "request_create_room" -> TODO("create room")
                "request_join_room" -> TODO("join room")
                "close_room" -> TODO("close room")
                "input_option" -> TODO("input option")
                "start_vote" -> TODO()
                "input_vote" -> TODO()
                "force_end_vote" -> TODO()
            }
        }
    }
}