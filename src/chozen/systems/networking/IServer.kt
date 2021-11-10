package chozen.systems.networking

interface IServer {
    fun acceptNewIClient() : IClient
    fun start()
}