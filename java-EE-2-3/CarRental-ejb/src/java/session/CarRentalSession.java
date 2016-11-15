package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    /*
    
    TODO: create classes/functions for all queries
    
    */
    
    
    
    @PersistenceContext
    EntityManager em;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(em.createQuery("SELECT CRC.name FROM CarRentalCompany CRC", String.class).getResultList());
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        // TODO: filter out possibilities here so getAvailableCarTypes(start, end) is not necessary
        TypedQuery q = em.createQuery("SELECT CRC FROM CarRentalCompany CRC JOIN CarType CT", CarRentalCompany.class);
        List<CarRentalCompany> companies = new ArrayList<CarRentalCompany>(q.getResultList());
        
        List<CarType> availableCarTypes = new LinkedList<CarType>();
        for(CarRentalCompany crc : companies) {
            for(CarType ct : crc.getAvailableCarTypes(start, end)) {
                if(!availableCarTypes.contains(ct))
                    availableCarTypes.add(ct);
            }
        }
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        try {
            CarRentalCompany c = em.createQuery("SELECT CRC FROM CarRentalCompany CRC WHERE CRC.name = :company", CarRentalCompany.class)
                                   .setParameter("company", company)
                                   .getSingleResult();
            
            Quote out = null;
            if (c != null) {
                out = c.createQuote(constraints, renter);
                quotes.add(out);
            }
            
            return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        CarRentalCompany c;
        try {
            for (Quote quote : quotes) {
                c = em.createQuery("SELECT CRC FROM CarRentalCompany CRC WHERE CRC.name = :company", CarRentalCompany.class)
                      .setParameter("company", quote.getRentalCompany())
                      .getSingleResult();
                
                if (c != null) {
                    done.add(c.confirmQuote(quote));
                }
                
                c = null;
            }
        } catch (Exception e) {
            for(Reservation r:done) {
                c = em.createQuery("SELECT CRC FROM CarRentalCompany CRC WHERE CRC.name = :company", CarRentalCompany.class)
                      .setParameter("company", r.getRentalCompany())
                      .getSingleResult();
                
                if (c!= null) {
                    c.cancelReservation(r);
                }
                
                c = null;
            }
            
            throw new ReservationException(e);
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }
}