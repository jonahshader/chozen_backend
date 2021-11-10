package chozen.systems

import chozen.systems.networking.IClient

class User(val socket: IClient) {
    var inRoom = false

    /**
     * directs inner room commands to room methods
     */
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
            "get_options" -> room.getOptions(this)
        }
    }

    /**
     * directs outer room commands to the server manager
     */
    fun pollCreateJoin(serverManager: ServerManager) {
        val line = socket.getMessage() ?: return
        println("pollCreateJoin received $line")
        val parts = line.split(' ')
        when (parts[0]) {
            "request_create_room" -> serverManager.createRoom(this)
            "request_join_room" -> serverManager.moveUserIntoRoom(this, parts[1])
        }
    }

    /**
     * send a message to this user
     */
    fun sendToUser(message: String) {
        println("sent $message")
        socket.sendMessage(message)
    }
}