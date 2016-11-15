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
            List<CarType> results = em.createQuery("SELECT carTypes FROM CarRentalCompany WHERE name = :name").setParameter("name", company).getResultList();
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
            List<Car> cars = em.createQuery("SELECT CRC.cars "
                                             + "FROM CarRentalCompany as CRC"
                                             + "JOIN Car "
                                             + "JOIN CarType "
                                             + "WHERE CRC.name = :company AND CarType.name = :type")
                                  .setParameter("name", company)
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
            return em.createQuery("SELECT COUNT(Reservation.id) "
                                             + "FROM CarRentalCompany as CRC"
                                             + "JOIN Car "
                                             + "JOIN Reservation "
                                             + "WHERE CRC.name = :company AND Car.id = :id")
                                  .setParameter("name", company)
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
            List<Car> cars = em.createQuery("SELECT CRC.cars "
                                             + "FROM CarRentalCompany as CRC"
                                             + "JOIN Car "
                                             + "JOIN CarType "
                                             + "WHERE CRC.name = :company AND CarType.name = :type")
                                  .setParameter("name", company)
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

}