package chozen;

import chozen.systems.ServerManager;
import chozen.systems.networking.CustomWebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketLauncher {
    public static void main(String[] args) {
        ServerManager sm = new ServerManager(new CustomWebSocketServer(new InetSocketAddress(25565)));
        sm.start();
    }
}
