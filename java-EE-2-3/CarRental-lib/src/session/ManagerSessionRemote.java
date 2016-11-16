package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public void createCarRentalCompany(String name);
    
    public void addRegions(String company, List<String> regions);
    
    public void addCarType(String company, Long id);
    
    public void addCar(String company, Long id);

    public Long createCarType(String name, int nrOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed);

    public Long createCar(Long typeID);
}