package chozen.systems.networking

import java.net.ServerSocket

class CustomTCPServer(port: Int) : IServer, ServerSocket(port) {
    override fun acceptNewIClient(): IClient {
        return CustomTCPClient(super.accept())
    }

    override fun start() {}
}