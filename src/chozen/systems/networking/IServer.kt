package chozen.systems.networking

interface IServer {
    fun accept() : IClient
    fun start()
}