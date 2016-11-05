package session;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import rentalAgency.RentalAgency;
import rental.Car;
import rental.CarRentalCompanyRemote;
import rental.CarType;
import rental.Reservation;

public class ManagerSession extends UnicastRemoteObject implements ManagerSessionRemote {

    private RentalAgency agency;

    public ManagerSession(RentalAgency agency) throws RemoteException {
        this.agency = agency;
    }

    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException {
        CarRentalCompanyRemote crc = this.agency.getRental(carRentalName);
        int reservationCount = 0;

        for (Car c : crc.getCars()) {
            if (c.getType().getName().equals(carType))
                reservationCount += c.getReservations().size();
        }

        return reservationCount;
    }

    @Override
    public Set<String> bestCustomer() throws RemoteException {
        /* Todo please make this more elegant. */

        Map<String, CarRentalCompanyRemote> crcmap = this.agency.getRentals();

        Map<String, Integer> customerScore = new HashMap<String, Integer>();

        for (CarRentalCompanyRemote crc : crcmap.values()) {
            for (Car c : crc.getCars()) {
                for (Reservation r : c.getReservations()) {
                    if (customerScore.containsKey(r.getCarRenter())) {
                        int score = customerScore.get(r.getCarRenter()) + 1;
                        customerScore.put(r.getCarRenter(), score);
                    } else {
                        customerScore.put(r.getCarRenter(), 1);
                    }
                }
            }
        }

        Map.Entry<String, Integer> maxEntry = null;

        for (Map.Entry<String, Integer> entry : customerScore.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        Set<String> ret = new HashSet<String>();

        for (Map.Entry<String, Integer> entry : customerScore.entrySet()) {
            if (entry.getValue().compareTo(maxEntry.getValue()) == 0) {
                ret.add(entry.getKey());
            }
        }


        return ret;
    }

    @Override
    public void registerCompany(CarRentalCompanyRemote remote) throws RemoteException {
        this.agency.registerCompany(remote);
    }

    @Override
    public void unregisterCompany(CarRentalCompanyRemote remote) throws RemoteException {
        this.agency.unregisterCompany(remote.getName());
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException {
        CarRentalCompanyRemote crcr = this.agency.getRental(carRentalCompanyName);
        Map<CarType, Integer> rescounts = new HashMap<>();

        for (Car c : crcr.getCars()) {
            for (Reservation r : c.getReservations()) {
                if (r.getStartDate().getYear() + 1900 == year) {
                    if (rescounts.containsKey(c.getType())) {
                        rescounts.put(c.getType(), rescounts.get(c.getType()) + 1);
                    } else {
                        rescounts.put(c.getType(), 1);
                    }
                }

            }
        }

        Map.Entry<CarType, Integer> maxEntry = null;

        for (Map.Entry<CarType, Integer> entry : rescounts.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        if(maxEntry == null) {
            System.out.println("Oh boy you fucked up " + String.valueOf(rescounts.size()));
        }
        return maxEntry.getKey();
    }
}
