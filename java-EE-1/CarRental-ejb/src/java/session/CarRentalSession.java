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
        Quote q = null;
        
        /* If we do not account for one car company being out active in a region
           but not having a car, reservations might fail and the expected totals
           will be wrong :( */
        
        
        for(CarRentalCompany c : RentalStore.getRentals().values()) {
            if(c.hasRegion(rc.getRegion())){
                crc = c;
                try{
                    q = crc.createQuote(rc, renter);
                    break;
                } catch (ReservationException e) {
                }
            }
        }
        
        if(crc != null && q != null) {
            quotes.add(q);
        } else {
            throw new ReservationException("No companies in region " + rc.getRegion() + " that satisfy the constraints.");
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
    
    @Override
    public Set<CarType> checkForAvailableCarTypes(Date start, Date end){
        Set<CarType> availableCars = new HashSet<CarType>();
        
        for (CarRentalCompany crc : RentalStore.getRentals().values()) {
            availableCars.addAll(crc.getAvailableCarTypes(start, end));
        }
        
        return availableCars;
    }  
    
}
