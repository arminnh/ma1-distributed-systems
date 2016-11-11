package rentalAgency;

import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RentalAgencyRemote extends Remote {

    ReservationSessionRemote getRentalSession(String name) throws RemoteException;

    ManagerSessionRemote getManagerSession() throws RemoteException;

    void removeReservationSession(String name) throws RemoteException;
}