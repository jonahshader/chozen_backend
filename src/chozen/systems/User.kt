package chozen.systems

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class User(val socket: Socket) {
    private val inputStream = socket.getInputStream()!!
    private val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val outputWriter = PrintWriter(socket.getOutputStream(), true)
    var inRoom = false

    fun pollRoomCommands(room: Room) {
        if (inputStream.available() > 0) {
            val line = bufferedReader.readLine()
            println("pollRoomCommands received $line")
            val parts = line.split(' ')
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
        if (inputStream.available() > 0) {
            val line = bufferedReader.readLine()
            println("pollCreateJoin received $line")
            val parts = line.split(' ')
            when (parts[0]) {
                "request_create_room" -> serverManager.createRoom(this)
                "request_join_room" -> serverManager.moveUserIntoRoom(this, parts[1])
            }
        }
    }

    fun sendToUser(message: String) {
        println("sent $message")
        outputWriter.println(message)
    }
}