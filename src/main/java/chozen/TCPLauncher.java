package chozen;

import chozen.systems.ServerManager;
import chozen.systems.networking.CustomTCPServer;

public class TCPLauncher {
    public static void main(String[] args) {
        ServerManager sm = new ServerManager(new CustomTCPServer(25565));
        sm.start();
    }
}
