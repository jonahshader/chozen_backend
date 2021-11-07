package chozen.systems

import chozen.systems.networking.IClient

class User(val socket: IClient) {
    var inRoom = false

    fun pollRoomCommands(room: Room) {
        val line = socket.getMessage() ?: return
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

    fun pollCreateJoin(serverManager: ServerManager) {
        val line = socket.getMessage() ?: return
        println("pollCreateJoin received $line")
        val parts = line.split(' ')
        when (parts[0]) {
            "request_create_room" -> serverManager.createRoom(this)
            "request_join_room" -> serverManager.moveUserIntoRoom(this, parts[1])
        }
    }

    fun sendToUser(message: String) {
        println("sent $message")
        socket.sendMessage(message)
    }
}