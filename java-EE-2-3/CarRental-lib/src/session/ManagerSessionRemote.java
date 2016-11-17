package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public void createCarRentalCompany(String name);

    public String createCarType(String id, int nrOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed);

    public Long createCar(String carTypeID);
    
    public void addRegions(String company, List<String> regions);
    
    public void addCarType(String company, String id);
    
    public void addCar(String company, Long id);

    public Set<String> getBestClients();

    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year);

    public int getNumberOfReservationsForCarType(String carRentalName, String carType);
}