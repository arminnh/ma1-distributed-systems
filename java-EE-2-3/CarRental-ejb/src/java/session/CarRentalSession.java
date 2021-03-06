package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {

    @PersistenceContext
    EntityManager em;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(em.createNamedQuery("CarRentalSession.getAllRentalCompanies", String.class).getResultList());
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarType> availableCarTypes = em.createNamedQuery("CarRentalSession.getAvailableCarTypes"
                                            , CarType.class)
                                            .setParameter("start",start)
                                            .setParameter("end", end)
                                            .getResultList();

        return availableCarTypes;
    }

    private Quote createQuote(String carRenter, ReservationConstraints constraints) throws ReservationException {
        List<CarRentalCompany> companies = em.createNamedQuery("CarRentalSession.createQuote", CarRentalCompany.class)
                                             .setParameter("region", constraints.getRegion())
                                             .setParameter("carType", constraints.getCarType())
                                             .getResultList();
        Quote q = null;

        if(companies.isEmpty())
            throw new ReservationException("No companies in region " + constraints.getRegion() + " that satisfy the constraints.");
        
        for (CarRentalCompany crc : companies) {
            try {
            System.out.println("Car types at company: " + crc.getName());
            for (CarType type : crc.getAllTypes()) {
                System.out.println("\t" + type.getName());
            }
            q = crc.createQuote(constraints, carRenter);           
            quotes.add(q);
            System.out.println("Added quote for: " + crc.getName());
            return q;
            } catch (ReservationException e) {}
        }
        
        if (q == null) {
            throw new ReservationException("No companies in region " + constraints.getRegion() + " that satisfy the constraints.");
        }
        return null;
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> reservations = new ArrayList<Reservation>();
        
        try {
            for (Quote quote : quotes) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, quote.getRentalCompany());
                System.out.println("Tried to find company with name: " + quote.getRentalCompany() + ", ");
                System.out.println(crc == null);
                
                if (crc != null) {
                    Reservation newReservation = crc.confirmQuote(quote);
                    em.persist(newReservation);
                    System.out.println("persisted reservation " + newReservation);
                    reservations.add(newReservation);
                }
            }
        } catch (Exception e) {
            
            throw new ReservationException(e);
        }

        // Assume quotes should be cleared.
        quotes.clear();
        
        return reservations;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getCheapestCarType(String region, Date start, Date end) {        
        Query q = em.createNamedQuery("CarRentalSession.getCheapestCarType", String.class);
        
        List<String> results = q.setParameter("region", region).setParameter("start",start).setParameter("end", end).getResultList();
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    public void addQuote(String carRenter, String carType, String region, Date start, Date end) throws ReservationException{
        ReservationConstraints rc = new ReservationConstraints(start, end, carType, region);
        this.createQuote(carRenter, rc);
    }
}