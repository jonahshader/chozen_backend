package chozen;

import chozen.systems.ServerManager;
import chozen.systems.networking.CustomTCPServer;

public class TCPLauncher {
    public static void main(String[] args) {
        ServerManager sm = new ServerManager(new CustomTCPServer(args.length == 1 ? Integer.parseInt(args[0]) : 25565));
        sm.start();
    }
}
