package session;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public interface ReservationSessionRemote {

    Set<String> getAllRentalCompanies() throws RemoteException;

    List<Quote> getCurrentQuotes() throws RemoteException;

    void createQuote(String renter, ReservationConstraints rc) throws ReservationException, RemoteException;

    List<Reservation> confirmQuotes() throws ReservationException, RemoteException;

    Set<CarType> checkForAvailableCarTypes(Date start, Date end) throws RemoteException;

    String getCheapestCarType(Date start, Date end, String region) throws RemoteException;
}
