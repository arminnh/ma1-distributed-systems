package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface CarRentalSessionRemote {

    Set<String> getAllRentalCompanies();
    
    List<Quote> getCurrentQuotes();
    
    void createQuote(String renter, ReservationConstraints rc) throws ReservationException;
    
    List<Reservation> confirmQuotes() throws ReservationException;
    
    Set<CarType> checkForAvailableCarTypes(Date start, Date end);
}
