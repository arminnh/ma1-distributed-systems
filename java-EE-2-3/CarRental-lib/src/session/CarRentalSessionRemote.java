package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationException;

@Remote
public interface CarRentalSessionRemote {
    
    public void setRenterName(String name);
    
    public Set<String> getAllRentalCompanies();
    
    public List<CarType> getAvailableCarTypes(Date start, Date end);
    
    public List<Quote> getCurrentQuotes();
    
    public List<Reservation> confirmQuotes() throws ReservationException;

    public String getCheapestCarType(String region, Date start, Date end);

    public void addQuote(String carRenter, String carType, String region, Date start, Date end) throws ReservationException;

    public List<Reservation> confirmQuotes(String carRenter) throws ReservationException;
    
}