package chozen;

import chozen.systems.ServerManager;

public class Launcher {
    public static void main(String[] args) {
        ServerManager sm = new ServerManager(25565);
        sm.start();
    }
}
