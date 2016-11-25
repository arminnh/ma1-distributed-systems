package session;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @PersistenceContext
    EntityManager em;

    @Override
    public Set<CarType> getCarTypes(String company) {
        List<CarType> results = em.createNamedQuery("ManagerSession.getCarTypes", CarType.class)
                .setParameter("name", company)
                .getResultList();
        return new HashSet<CarType>(results);
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List<Integer> carIds = em.createNamedQuery("ManagerSession.getCarIds")
                .setParameter("company", company)
                .setParameter("type", type)
                .getResultList();

        return new HashSet<Integer>(carIds);
    }

    @Override
    public void createCarRentalCompany(String name) {
        /* transactions are container managed. */
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
        Car car = em.createNamedQuery("ManagerSession.addCar", Car.class)
                .setParameter("id", id)
                .getSingleResult();
        crc.addCar(car);

        em.persist(crc);
    }

    @Override
    public Set<String> getBestClients() {
        List<Object[]> bestClients = em.createNamedQuery("ManagerSession.getBestClients")
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
        String carType = (String) getFirstResultOrNull(em.createNamedQuery("ManagerSession.getMostPopularCarTypeIn")
                .setParameter("company", carRentalName)
                .setParameter("thisYear", (new GregorianCalendar(year, 0, 0)).getTime())
                .setParameter("nextYear", (new GregorianCalendar(year + 1, 0, 0)).getTime()));
        return em.find(CarType.class, carType);
    }

    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) {
        Long count = (Long) getFirstResultOrNull(em.createNamedQuery("ManagerSession.getNumberOfReservationsForCarType")
                .setParameter("company", carRentalName)
                .setParameter("type", carType));

        return count.intValue();
    }

    public static Object getFirstResultOrNull(Query query) {
        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}
