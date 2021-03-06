package chozen.systems

import chozen.systems.networking.IServer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class ServerManager(private val serverSocket: IServer) {
    private val usersNotInRoomLock = ReentrantLock()
    private val usersNotInRoom = mutableListOf<User>()
    private val rooms = ConcurrentHashMap<String, Room>()

    fun start() {
        serverSocket.start()
        // run a thread for gathering new user connections
        thread {
            while (true) {
                val newUser = serverSocket.acceptNewIClient()
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

    /**
     * creates a new room with a unique room id
     * reply to requester "room_id <the new room's id>"
     */
    fun createRoom(requester: User) {
        val newRoom = RoomGenerator.createUniqueRoom(rooms)
        rooms[newRoom.id] = newRoom
        requester.sendToUser("room_id ${newRoom.id}")
    }

    /**
     * attempts to move specified user into room with roomId.
     * if this room exists, move user into room and reply "request_join_room_success"
     * if this room does not exist, reply "request_join_room_failed"
     */
    fun moveUserIntoRoom(user: User, roomId: String) {
        user.inRoom = true
        val room = rooms[roomId]
        if (room == null) {
            println("WARNING: User tried joining a room that doesn't exist!")
            user.sendToUser("request_join_room_failed")
        } else {
            rooms[roomId]!!.addUser(user)
            user.sendToUser("request_join_room_success")
        }
    }
}