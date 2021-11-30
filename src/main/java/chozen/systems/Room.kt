package chozen.systems

import chozen.config.Configuration.OPTION_RELAY_ENABLED
import java.util.concurrent.ConcurrentHashMap

class Room(val id: String) {
    enum class RoomState {
        ROOM_OPEN,
        ROOM_CLOSED_ACCEPTING_OPTIONS,
        VOTING,
        DONE
    }

    init {
        println("room $id created")
    }

    private var state = RoomState.ROOM_OPEN // room is initially open upon creation
    private val users = mutableListOf<User>()
    private var totalVotes = 0
    private val options = ConcurrentHashMap<String, Int>()
    private var roomTopic = ""

    fun update() {
        // remove users that are disconnected
        users.removeIf { it.socket.isClosed() }
        users.forEach {
            it.pollRoomCommands(this)
        }
        // if the state is voting and we have all the votes,
        if (state == RoomState.VOTING && totalVotes == users.size * options.size) {
            computeAndSendWinner()
        }
    }

    fun addUser(user: User) {
        users += user
    }

    /**
     * if the state is ROOM_OPEN, change to the ROOM_CLOSED_ACCEPTING_OPTIONS state
     * and notify all users in room that the room was closed
     */
    fun closeRoom() {
        if (state == RoomState.ROOM_OPEN) {
            state = RoomState.ROOM_CLOSED_ACCEPTING_OPTIONS

            // send message to all users to signal close room state change
            users.forEach {
                it.sendToUser("close_room")
            }
            println("room $id closed")
        }
    }

    /**
     * if the state is ROOM_CLOSED_ACCEPTING_OPTIONS and the option doesn't exist already,
     * add it and set the initial votes to zero
     * then notify all users in room that this option was created
     */
    fun addOption(option: String) {
        if (state == RoomState.ROOM_CLOSED_ACCEPTING_OPTIONS && !options.contains(option)) {
            options[option] = 0
            // send message to all users in room to add option (if functionality enabled)
            if (OPTION_RELAY_ENABLED) {
                users.forEach {
                    it.sendToUser("add_option $option")
                }
            }

            println("added $option option to room $id")
        }
    }

    /**
     * if the state is ROOM_CLOSED_ACCEPTING_OPTIONS,
     * switch to VOTING state and notify all users in room
     */
    fun startVote() {
        if (state == RoomState.ROOM_CLOSED_ACCEPTING_OPTIONS) {
            state = RoomState.VOTING
            users.forEach {
                it.sendToUser("start_vote")
            }
            println("room $id in voting mode")
        }
    }

    /**
     * check to see if option exists.
     * if it does, increment its vote count and the total vote count by one
     */
    fun inputVote(option: String, yes: Boolean) {
        if (options.containsKey(option)) {
            if (yes) {
                options[option] = options[option]!! + 1
            }
            totalVotes++
        } else {
            println("ERROR: room $id received invalid vote $option $yes")
            TODO("add exception here")
        }
    }

    /**
     * forces the voting stage to end and runs computeAndSendWinner()
     */
    fun forceEndVote() {
        if (state == RoomState.VOTING) {
            computeAndSendWinner()
        }
    }

    private fun computeAndSendWinner() {
        println("room $id computing winner")
        var bestOptionVoteCount = 0
        for (v in options) {
            if (v.value > bestOptionVoteCount) {
                bestOptionVoteCount = v.value
            }
        }
        val bestOptions = mutableListOf<String>()
        for (v in options) {
            if (v.value == bestOptionVoteCount) {
                bestOptions += v.key
            }
        }
        // take a random one from the best (tie-breaker)
        val selectedBestOption = bestOptions.random()

        users.forEach {
            it.sendToUser("winning_option $selectedBestOption")
        }

        shutdown() // hopefully the messages get send before this is called
        state = RoomState.DONE
        println("room $id $selectedBestOption won")
    }

    fun isDone() = state == RoomState.DONE

    private fun shutdown() {
        // close sockets
        users.forEach { it.socket.close() }
        users.clear()
    }

    /**
     * returns "options " followed by a space delimited list of all options
     */
    fun getOptions(user: User) {
        val optionsAsString = options.keys.fold("") { acc, s -> "$acc $s" }
        user.sendToUser("options$optionsAsString")
    }

    fun setTopic(topic: String) {
        // update topic and notify all users
        roomTopic = topic

        users.forEach { getTopic(it) }
    }

    fun getTopic(user: User) {
        user.sendToUser("topic $roomTopic")
    }
}