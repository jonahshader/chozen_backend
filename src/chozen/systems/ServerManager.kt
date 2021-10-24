package chozen.systems

import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class ServerManager(port: Int) {
    private val serverSocket = ServerSocket(port)
    private val usersNotInRoomLock = ReentrantLock()
    private val usersNotInRoom = mutableListOf<User>()
    private val rooms = ConcurrentHashMap<String, Room>()

    fun start() {
        // run a thread for gathering new user connections
        thread {
            while (true) {
                val newUser = serverSocket.accept()
                usersNotInRoomLock.lock()
                usersNotInRoom += User(newUser)
                usersNotInRoomLock.unlock()
            }
        }

        // continuously update rooms, and pull inputs from users that aren't in a room yet
        // if one of these users requests to join a room, move user to room
        // if one of these users requests to create a room, then create a room with unique id
        while (true) {
            usersNotInRoomLock.lock()
            usersNotInRoom.forEach { it.pollCreateJoin(this) }
            usersNotInRoom.removeIf { it.inRoom }
            usersNotInRoomLock.unlock()

            val roomsToRemove = mutableListOf<String>()
            rooms.forEach {
                it.value.update()
                // if the room is done, then add it to the list of rooms to remove
                if (it.value.isDone()) {
                    roomsToRemove += it.key
                }
            }
            // remove done rooms
            roomsToRemove.forEach {
                println("removed room $it")
                rooms.remove(it)
            }
        }
    }

    fun createRoom(requester: User) {
        val newRoom = RoomGenerator.createRoom(rooms)
        rooms[newRoom.id] = newRoom
        requester.sendToUser("room_id ${newRoom.id}")
    }

    fun moveUserIntoRoom(user: User, roomId: String) {
        user.inRoom = true
        rooms[roomId]!!.addUser(user)
        user.sendToUser("request_join_room_success")
    }
}