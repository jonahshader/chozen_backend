package chozen.systems.networking

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

class CustomTCPClient(private val socket: Socket) : IClient {
    private val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val outputWriter = PrintWriter(socket.getOutputStream(), true)


    override fun getMessage(): String? {
        return if (socket.getInputStream().available() > 0) {
            bufferedReader.readLine()
        } else {
            null
        }
    }

    override fun isClosed(): Boolean = socket.isClosed

    override fun sendMessage(message: String) {
        outputWriter.println(message)
    }

    override fun close() {
        socket.close()
    }
}