package session;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    // TODO move this to a JPQL queries/helper class
    public static Object getFirstResultOrNull(Query query){
        List results = query.getResultList();
        if (results.isEmpty()) return null;
        return results.get(0);
    }
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        List<CarType> results = em.createQuery("SELECT CT FROM CarRentalCompany CRC JOIN CRC.carTypes CT WHERE CRC.name = :name", CarType.class)
                                  .setParameter("name", company)
                                  .getResultList();
        return new HashSet<CarType>(results);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List<Integer> carIds = em.createQuery("SELECT C.id FROM CarRentalCompany as CRC JOIN CRC.cars C JOIN CRC.carTypes CT WHERE CRC.name = :company AND CT.name = :type")
                                 .setParameter("company", company)
                                 .setParameter("type", type)
                                 .getResultList();
        
        return new HashSet<Integer>(carIds);
    }

    @Override
    public void createCarRentalCompany(String name) {
        /*
            em.getTransaction().begin(); // TODO CHECK: do we need to do this here? 
            http://stackoverflow.com/questions/10915855/cannot-use-an-entitytransaction-while-using-jta
            In your case the transaction is managed by the container, in the first use of the EntitiyManager in your method, 
            the container checks whether there is an active transaction or not, if there is no transaction active then it creates one, 
            and when the method call ends, the transaction is committed by the container. The container takes care of the transaction, that is JTA.
        */
        
        em.persist(new CarRentalCompany(name));
    }

    @Override
    public String createCarType(String id, int nrOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        if (em.find(CarType.class, id) != null) {
            return id;
        }
        
        CarType type = new CarType(id, nrOfSeats, trunkSpace, rentalPricePerDay, smokingAllowed);
        
        em.persist(type);
        return type.getName();
    }
    
    @Override
    public Long createCar(String carTypeID) {
        Car car = new Car(em.find(CarType.class, carTypeID));
        
        em.persist(car);
        return (long) car.getId();
    }

    @Override
    public void addRegions(String company, List<String> regions) {
        CarRentalCompany crc = em.find(CarRentalCompany.class, company);
        crc.addRegions(regions);
        
        em.persist(crc);
    }

    @Override
    public void addCarType(String company, String id) {
        CarRentalCompany crc = em.find(CarRentalCompany.class, company);
        crc.addCarType(em.find(CarType.class, id));
        em.persist(crc);
    }

    @Override
    public void addCar(String company, Long id) {
        CarRentalCompany crc = em.find(CarRentalCompany.class, company);
        Car car = em.createQuery("SELECT C FROM Car C WHERE C.id = :id", Car.class)
                    .setParameter("id", id)
                    .getSingleResult();
        crc.addCar(car);
        
        em.persist(crc);
    }

    @Override
    public Set<String> getBestClients() {
        /* Jago pls fix
        List<Object[]> bestClients = em.createQuery("SELECT R.carRenter, COUNT(R) as nr FROM Reservation R WHERE nr = (SELECT MAX(COUNT(R)) FROM Reservation R GROUP BY R.carRenter) GROUP BY R.carRenter").getResultList();
        Set<String> results = new HashSet<String>();
        
        for (Object[] array : bestClients) {
            results.add((String) array[0]);
        }
        */
        
        List<Object[]> bestClients = em.createQuery("SELECT R.carRenter, COUNT(R) as amount FROM Reservation R GROUP BY R.carRenter ORDER BY amount DESC")
                                       .getResultList();
        
        Set<String> results = new HashSet<String>();
        Long max = (Long) bestClients.get(0)[1];
        
        for (Object[] array : bestClients) {
            Long count = (Long) array[1];
            if (count.equals(max)) {
                results.add((String) array[0]);
            }
        }
        
        return results;
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalName, int year) {
        String carType = (String) getFirstResultOrNull(em.createQuery("SELECT R.carType FROM Reservation R WHERE R.rentalCompany = :company AND R.startDate > :thisYear AND R.startDate < :nextYear GROUP BY R.carType ORDER BY COUNT(R.id) DESC")
                                                         .setParameter("company", carRentalName)
                                                         .setParameter("thisYear", (new GregorianCalendar(year, 0, 0)).getTime())
                                                         .setParameter("nextYear", (new GregorianCalendar(year + 1, 0, 0)).getTime()));
        return em.find(CarType.class, carType);
    }

    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) {
        Long count = (Long) getFirstResultOrNull(em.createQuery("SELECT count(R) FROM Reservation R WHERE R.rentalCompany = :company AND R.carType = :type")
                                                   .setParameter("company", carRentalName)
                                                   .setParameter("type", carType));
        
        return count.intValue();
    }

}