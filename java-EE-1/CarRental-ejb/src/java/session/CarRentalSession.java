package session;

import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateful;
import rental.*;
import rental.ReservationConstraints;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    List<Quote> quotes = new ArrayList<Quote>();
    
    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }
    
    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public void createQuote(String renter, ReservationConstraints rc) throws ReservationException {
        CarRentalCompany crc = null;
        
        for(CarRentalCompany c : RentalStore.getRentals().values()) {
            if(c.hasRegion(rc.getRegion())){
                crc = c;
                break;
            }
        }
        
        if(crc != null) {
            quotes.add(crc.createQuote(rc, renter));
        } else {
            throw new ReservationException("No companies in region " + rc.getRegion());
        }  
    }
    
    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> reservations = new ArrayList<Reservation>();
        
        try {
            
            for(Quote q : quotes) {
                CarRentalCompany crc = RentalStore.getRental(q.getRentalCompany());
                reservations.add(crc.confirmQuote(q));
            }
            
        } catch (ReservationException e) {
            
            System.out.println(e.toString());
            
            for(Reservation r : reservations) {
                CarRentalCompany crc = RentalStore.getRental(r.getRentalCompany());
                crc.cancelReservation(r);
            }
            
            throw new ReservationException("A Reservation could not be finalized.");
        
        }
        
        // Assume quotes should be cleared.
        quotes.clear();
                
        return reservations;
    }
    
    public Set<CarType> checkForAvailableCarTypes(Date start, Date end){
        Set<CarType> availableCars = new HashSet<CarType>();
        
        for (CarRentalCompany crc : RentalStore.getRentals().values()) {
            availableCars.addAll(crc.getAvailableCarTypes(start, end));
        }
        
        return availableCars;
    }
    
    
    
}
