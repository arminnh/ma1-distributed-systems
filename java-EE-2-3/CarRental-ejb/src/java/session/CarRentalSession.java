package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {
    
    // TODO move this to a JPQL queries/helper class
    public static Object getSingleResultOrNull(Query query){
        List results = query.getResultList();
        if (results.isEmpty()) return null;
        else if (results.size() == 1) return results.get(0);
        throw new NonUniqueResultException();
    }
    
    // TODO move this to a JPQL queries/helper class
    public static Object getFirstResultOrNull(Query query){
        List results = query.getResultList();
        if (results.isEmpty()) return null;
        return results.get(0);
    }

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
        TypedQuery q = em.createQuery("SELECT CRC FROM CarRentalCompany CRC JOIN CRC.carTypes CT", CarRentalCompany.class);
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

    private Quote createQuote(String carRenter, ReservationConstraints constraints) throws ReservationException {
        List<CarRentalCompany> companies = em.createQuery("SELECT CRC FROM CarRentalCompany CRC JOIN CRC.carTypes CT WHERE CT.name = :carType AND :region MEMBER OF CRC.regions", CarRentalCompany.class)
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
            for(Reservation r : reservations) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, r.getRentalCompany());
                System.out.println("Tried to find company with name: " + r.getRentalCompany() + ", ");
                System.out.println(crc == null);
                
                if (crc!= null) {
                    crc.cancelReservation(r);
                    em.remove(r);
                    System.out.println("removed reservation " + r);
                }
            }
            
            throw new ReservationException(e);
        }

        // Assume quotes should be cleared.
        quotes.clear();
        
        return reservations;
    }

    @Override
    public List<Reservation> confirmQuotes(String carRenter) throws ReservationException {
        // TODO: does carRenter name matter? what's up with all the extra arguments in this assignment >:(
        return this.confirmQuotes();
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public Set<CarType> checkForAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCars = new HashSet<CarType>();
        List<CarRentalCompany> companies = em.createQuery("SELECT CRC FROM CarRentalCompany CRC", CarRentalCompany.class).getResultList();
        
        for (CarRentalCompany crc : companies) {
            availableCars.addAll(crc.getAvailableCarTypes(start, end));
        }

        return availableCars;
    }

    @Override
    public String getCheapestCarType(String region, Date start, Date end) {        
        Query q = em.createQuery(""
                + "SELECT CT.name "
                + "FROM CarRentalCompany CRC JOIN CRC.carTypes CT JOIN CRC.cars C "
                + "WHERE :region MEMBER OF CRC.regions AND C.type = CT "
                + "AND C.id NOT IN ( "
                +   "SELECT R.carId "
                +   "FROM CarRentalCompany CRC JOIN CRC.cars CC JOIN CC.reservations CR JOIN Reservation R "
                +   "WHERE R.startDate <= :end AND R.endDate >= :start AND :region MEMBER OF CRC.regions ) "
                + "ORDER BY CT.rentalPricePerDay", String.class);
        
        return (String) getFirstResultOrNull(q.setParameter("region", region).setParameter("start",start).setParameter("end", end));
    }

    @Override
    public void addQuote(String carRenter, String carType, String region, Date start, Date end) throws ReservationException{
        ReservationConstraints rc = new ReservationConstraints(start, end, carType, region);
        this.createQuote(carRenter, rc);
    }
}