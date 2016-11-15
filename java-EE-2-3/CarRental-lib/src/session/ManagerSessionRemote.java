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
      
    public void addCarRentalCompany(String name);
    
    public void addRegions(String company, List<String> regions);
    
    public void addCarType(String company, String type);
    
    public void addCar(String company, String type);
}