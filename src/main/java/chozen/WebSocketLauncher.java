package chozen;

import chozen.systems.ServerManager;
import chozen.systems.networking.CustomWebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketLauncher {
    public static void main(String[] args) {
        ServerManager sm;
        sm = new ServerManager(new CustomWebSocketServer(new InetSocketAddress(args.length == 1 ? Integer.parseInt(args[0]) : 25565)));
        sm.start();
    }
}
