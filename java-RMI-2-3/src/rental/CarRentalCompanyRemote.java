package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CarRentalCompanyRemote extends Remote {
    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

    Quote createQuote(ReservationConstraints constraints, String guest) throws ReservationException, RemoteException;

    Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;

    void cancelReservation(Reservation res) throws RemoteException;

    Set<Reservation> getReservationsBy(String renter) throws RemoteException;

    String getName() throws RemoteException;

    List<Car> getCars() throws RemoteException;

    boolean hasRegion(String region) throws RemoteException;
}
