package chozen.systems

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.ConcurrentHashMap

internal class RoomGeneratorTest {

    @Test
    fun createUniqueRoom() {
        // create concurrentHashMap of rooms
        val rooms = ConcurrentHashMap<String, Room>()

        val samples = 100000
        // generate a large number of rooms
        for (i in 0 until samples) {
            val newRoom = RoomGenerator.createUniqueRoom(rooms)
            rooms[newRoom.id] = newRoom
        }
        // hashmaps cannot contain duplicates, so if the
        // number of rooms in the hashmap is 100000 exactly, then
        // createUniqueRoom() has successfully created unique rooms
        // with no duplicates
        assertEquals(samples, rooms.size)
    }
}