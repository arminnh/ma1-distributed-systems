package rentalAgency;

import rental.CarRentalCompanyRemote;
import session.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RentalAgency implements RentalAgencyRemote {

    private Map<String, CarRentalCompanyRemote> rentals = new HashMap<>();
    private Map<String, ManagerSession> managerSessionMap = new HashMap<>();
    private Map<String, ReservationSession> reservationSessionMap = new HashMap<>();

    public static void main(String[] args) throws Exception {

        System.setSecurityManager(null);
        RentalAgency agency = new RentalAgency();

        try {
            RentalAgencyRemote agencyRemote = (RentalAgencyRemote) UnicastRemoteObject.exportObject(agency, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("RentalAgency", agencyRemote);

            System.out.println("RentalAgency is now running!.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ReservationSessionRemote getRentalSession(String name) throws RemoteException {
        if (!reservationSessionMap.containsKey(name)) {
            reservationSessionMap.put(name, new ReservationSession(this));
        }
        return reservationSessionMap.get(name);
    }

    @Override
    public ManagerSessionRemote getManagerSession(String name) throws RemoteException {
        if (!managerSessionMap.containsKey(name)) {
            managerSessionMap.put(name, new ManagerSession(this));
        }
        return managerSessionMap.get(name);
    }

    @Override
    public void removeSession() {
        //todo
    }

    public CarRentalCompanyRemote getRental(String company) {
        CarRentalCompanyRemote out = this.getRentals().get(company);
        if (out == null) {
            throw new IllegalArgumentException("Company doesn't exist!: " + company);
        }
        return out;
    }

    public synchronized Map<String, CarRentalCompanyRemote> getRentals() {
        return rentals;
    }

    public void registerCompany(CarRentalCompanyRemote cr) throws RemoteException {
        String cname = cr.getName();
        if (rentals.containsKey(cname)) {
            System.out.println("Company with name " + cr.getName() + " is already registered");
        } else {
            rentals.put(cr.getName(), cr);
            System.out.println("Registered company " + cr.getName());
        }
    }

    public void unregisterCompany(String name) {
        if (rentals.containsKey(name)) {
            rentals.remove(name);
            System.out.println("unregistered company " + name);
        } else {
            System.out.println("Company with name " + name + " isn't registered");
        }
    }
}