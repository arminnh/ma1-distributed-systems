package nameserver;

import rental.Car;
import rental.CarRentalCompany;
import rental.CarRentalCompanyRemote;
import session.ReservationSession;
import session.ReservationSessionRemote;
import session.ManagerSession;
import session.ManagerSessionRemote;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RentalAgency {

    private static Map<String, CarRentalCompanyRemote> rentals = new HashMap<>();


    public static CarRentalCompanyRemote getRental(String company) {
        CarRentalCompanyRemote out = RentalAgency.getRentals().get(company);
        if (out == null) {
            throw new IllegalArgumentException("Company doesn't exist!: " + company);
        }
        return out;
    }
    
    public static synchronized Map<String, CarRentalCompanyRemote> getRentals(){
        return rentals;
    }

    public static void registerCompany(CarRentalCompanyRemote cr) throws RemoteException{
        String cname = cr.getName();
        if(rentals.containsKey(cname)) {
            System.out.println("Company with name " + cr.getName() + " is already registered");
        } else {
            rentals.put(cr.getName(), cr);
            System.out.println("Registered company " + cr.getName());
        }
    }

    public static void unregisterCompany(String name) {
        if(rentals.containsKey(name)) {
            rentals.remove(name);
            System.out.println("unregistered company " + name);
        } else {
            System.out.println("Company with name " + name + " isn't registered");
        }
    }
}