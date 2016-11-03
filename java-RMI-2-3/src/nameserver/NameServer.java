package nameserver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class NameServer {
    public static void main(String[] args) throws Exception {

        System.setSecurityManager(null);
        SessionManager sm = new SessionManager();

        try {
            SessionManagerRemote ir = (SessionManagerRemote) UnicastRemoteObject.exportObject(sm,0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("RentalAgency",ir);
            System.out.println("Rental agency is now running.");
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
