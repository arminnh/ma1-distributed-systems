package session;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SessionManagerRemote extends Remote {
    ReservationSessionRemote getRentalSession(String name) throws RemoteException;

    ManagerSessionRemote getManagerSession(String name) throws RemoteException;

    void removeSession() throws RemoteException;
}
