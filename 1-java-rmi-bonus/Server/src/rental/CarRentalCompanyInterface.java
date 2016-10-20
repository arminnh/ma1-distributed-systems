package rental;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CarRentalCompanyInterface extends Remote {

    Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

    Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, RemoteException;

    Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;

    List<Reservation> getReservationsByRenter(String clientName) throws RemoteException;

    int getNumberOfReservationsForCarType(String carType) throws RemoteException;

}
