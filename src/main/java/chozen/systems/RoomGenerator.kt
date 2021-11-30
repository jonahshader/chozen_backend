package chozen.systems

import java.util.*
import java.util.concurrent.ConcurrentHashMap

object RoomGenerator {
    private val rand = Random()
    private val roomIDLength = 5
    private val validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

    /**
     * creates a room with a unique ID
     */
    fun createUniqueRoom(rooms: ConcurrentHashMap<String, Room>) : Room {
        var newKey = generateRandomRoomID()
        while (rooms.containsKey(newKey)) {
            newKey = generateRandomRoomID()
        }
        return Room(newKey)
    }

    private fun generateRandomRoomID() : String {
        var output = ""
        for (i in 0 until roomIDLength) {
            output += validChars[rand.nextInt(validChars.length)]
        }

        return output
    }
}