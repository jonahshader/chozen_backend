package chozen.systems

import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class User(val socket: Socket) {
    private val inputStreamReader = InputStreamReader(socket.getInputStream())
    private val outputWriter = PrintWriter(socket.getOutputStream(), true)

    fun pollRoomCommands(room: Room) {
        inputStreamReader.forEachLine {
            val parts = it.split(' ')
            when (parts[0]) {
                "close_room" -> room.closeRoom()
                "add_option" -> room.addOption(parts[1])
                "start_vote" -> room.startVote()
                "input_vote" -> room.inputVote(parts[1], parts[2] == "yes")
                "force_end_vote" -> room.forceEndVote()
            }
        }
    }

    fun pollCreateJoin(serverManager: ServerManager) {
        inputStreamReader.forEachLine {
            val parts = it.split(' ')
            when (parts[0]) {
                "request_create_room" -> serverManager.createRoom(this)
                "request_join_room" -> serverManager.moveUserIntoRoom(this, parts[1])
            }
        }
    }

    fun sendToUser(message: String) {
        outputWriter.println(message)
    }
}