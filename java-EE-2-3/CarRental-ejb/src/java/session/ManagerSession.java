package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            List<CarType> results = em.createQuery("SELECT CT FROM CarRentalCompany CRC JOIN CarType CT WHERE CRC.name = :name", CarType.class)
                                      .setParameter("name", company)
                                      .getResultList();
            return new HashSet<CarType>(results);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            List<Car> cars = em.createQuery("SELECT C FROM CarRentalCompany as CRC JOIN Car C JOIN CarType CT WHERE CRC.name = :company AND CT.name = :type")
                               .setParameter("company", company)
                               .setParameter("type", type)
                               .getResultList();
            
            for(Car c: cars){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return em.createQuery("SELECT COUNT(R.id) FROM CarRentalCompany CRC JOIN Car C JOIN Reservation R WHERE CRC.name = :company AND C.id = :id", Integer.class)
                     .setParameter("company", company)
                     .setParameter("id", id)
                     .getFirstResult();
            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            List<Car> cars = em.createQuery("SELECT C FROM CarRentalCompany as CRC JOIN Car C JOIN CarType CT WHERE CRC.name = :company AND CT.name = :type")
                               .setParameter("company", company)
                               .setParameter("type", type)
                               .getResultList();
            
            for(Car c: cars){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
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
    public Long createCarType(String name, int nrOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        CarType type = new CarType(name, nrOfSeats, trunkSpace, rentalPricePerDay, smokingAllowed);
        
        em.persist(type);
        return type.getId();
    }
    
    @Override
    public Long createCar(Long typeID) {
        CarType ct = em.createQuery("SELECT CT FROM CarType CT WHERE CT.id = :id", CarType.class).setParameter("id", typeID).getSingleResult();
        Car car = new Car(ct);
        
        em.persist(car);
        return (long) car.getId();
    }

    @Override
    public void addRegions(String company, List<String> regions) {
        // TODO: refactor queries
        CarRentalCompany crc = em.createQuery("SELECT CRC FROM CarRentalCompany CRC WHERE CRC.name = :company", CarRentalCompany.class).setParameter("company", company).getSingleResult();
        crc.addRegions(regions);
        
        em.persist(crc);
    }

    @Override
    public void addCarType(String company, Long id) {
        CarRentalCompany crc = em.createQuery("SELECT CRC FROM CarRentalCompany CRC WHERE CRC.name = :company", CarRentalCompany.class).setParameter("company", company).getSingleResult();
        CarType ct = em.createQuery("SELECT CT FROM CarType CT WHERE CT.id = :id", CarType.class).setParameter("id", id).getSingleResult();
        crc.addCarType(ct);
        
        em.persist(crc);
    }

    @Override
    public void addCar(String company, Long id) {
        CarRentalCompany crc = em.createQuery("SELECT CRC FROM CarRentalCompany CRC WHERE CRC.name = :company", CarRentalCompany.class).setParameter("company", company).getSingleResult();
        Car car = em.createQuery("SELECT C FROM Car C WHERE C.id = :id", Car.class).setParameter("id", id).getSingleResult();
        crc.addCar(car);
        
        em.persist(crc);
    }

}