package chozen.custom_tests

import chozen.systems.ServerManager
import chozen.systems.networking.CustomTCPClient
import chozen.systems.networking.CustomTCPServer
import chozen.systems.networking.IClient
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.system.exitProcess
import kotlin.test.assertEquals

fun main() {
    val numTestClients = 25
    val port = 22345

    // launch server (in separate thread to mimic server being on separate pc)
    thread {
        val server = ServerManager(CustomTCPServer(port))
        server.start()
    }

    // wait some time
    Thread.sleep(100)

    // create fake clients
    val clients = mutableListOf<IClient>()
    for (i in 0 until numTestClients) clients += CustomTCPClient(Socket("localhost", port))

    // clients[0] will be host
    clients[0].sendMessage("request_create_room")

    // wait some time
    Thread.sleep(100)

    // expecting room_id <id>, we want the <id> part so split at space and take second part
    val roomId = clients[0].getMessage()!!.split(' ')[1]

    // make all clients connect
    clients.forEach {
        it.sendMessage("request_join_room $roomId")
    }

    // wait some time
    Thread.sleep(100)

    // check to see all users joined successfully
    clients.forEach {
        assertEquals("request_join_room_success", it.getMessage()!!)
    }

    // wait some time
    Thread.sleep(100)

    // host closes room
    clients[0].sendMessage("close_room")

    // wait some time
    Thread.sleep(100)

    // check to see all users received "close_room"
    clients.forEach {
        assertEquals("close_room", it.getMessage()!!)
    }

    // wait some time
    Thread.sleep(100)

    // all users add one option corresponding to their index in list
    clients.forEachIndexed { index, iClient ->  iClient.sendMessage("add_option $index")}

    // wait some time
    Thread.sleep(100)

    // hosts starts voting
    clients[0].sendMessage("start_vote")

    // wait some time
    Thread.sleep(100)

    // get start_vote echoes from all users
    clients.forEach {
        assertEquals("start_vote", it.getMessage()!!)
    }

    // wait some time
    Thread.sleep(100)

    // clients vote randomly (only care about stress testing here)
    clients.forEach {
        for (i in clients.indices) {
            it.sendMessage("input_vote $i ${if (Math.random() > .5) "yes" else "no"}")
//            Thread.sleep(5)
        }
    }

    // wait some time
    Thread.sleep(100)

    // get winning result from one user
    val winningMessage = clients[0].getMessage()!!

    // make sure other clients get same message
    for (i in 1 until clients.size) {
        assertEquals(winningMessage, clients[i].getMessage()!!)
    }

    println("winning message: $winningMessage")
    exitProcess(0)
}