/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import java.util.Set;
import rental.*;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @Override
    public Set<CarType> getCarTypes(String name, Date start, Date end) {
        return RentalStore.getRental(name).getAvailableCarTypes(start, end);
    }

    @Override
    public int getNumerOfReservationsForCarType(String carRentalName, String carType) {
        CarRentalCompany crc = RentalStore.getRental(carRentalName);
        int reservationCount = 0;
        
        for(Car c: crc.getCars()){
            reservationCount += c.getReservations().size();
        }
        
        return reservationCount;
    }
    
    @Override
    public String bestCustomer() {
        Map<String,CarRentalCompany> crcmap = RentalStore.getRentals();
        Map<String,Integer> customerScore = new HashMap<String,Integer>();
        
        for(CarRentalCompany crc : crcmap.values()) {
            for(Car c : crc.getCars()) {
                for(Reservation r : c.getReservations()){
                    if(customerScore.containsKey(r.getCarRenter())){
                        int score = customerScore.get(r.getCarRenter());
                        customerScore.put(r.getCarRenter(), score);
                    } else {
                        customerScore.put(r.getCarRenter(), 1);
                    }
                }
            }
        }
        
        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : customerScore.entrySet()){
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                maxEntry = entry;
            }
        }
        
        return maxEntry.getKey();
    }
}
