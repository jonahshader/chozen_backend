package chozen.systems.networking

interface IClient {
    fun getMessage() : String?
    fun isClosed() : Boolean
    fun sendMessage(message: String)
    fun close()
}