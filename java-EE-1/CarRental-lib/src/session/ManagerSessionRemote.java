/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.util.Date;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;


@Remote
public interface ManagerSessionRemote {

    Set<CarType> getCarTypes(String name, Date start, Date end);
            
    int getNumerOfReservationsForCarType(String carRentalName, String carType);
    
    String bestCustomer();
}
