package session;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import nameserver.RentalAgency;
import rental.Car;
import rental.CarRentalCompanyRemote;
import rental.CarType;
import rental.Reservation;

public class ManagerSession implements ManagerSessionRemote {

    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException {
        CarRentalCompanyRemote crc = RentalAgency.getRental(carRentalName);
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

        Map<String, CarRentalCompanyRemote> crcmap = RentalAgency.getRentals();

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
        RentalAgency.registerCompany(remote);
    }

    @Override
    public void unregisterCompany(CarRentalCompanyRemote remote) throws RemoteException {
        RentalAgency.unregisterCompany(remote.getName());
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException {
        CarRentalCompanyRemote crcr = RentalAgency.getRental(carRentalCompanyName);
        Map<CarType, Integer> rescounts = new HashMap<>();

        for (Car c : crcr.getCars()) {
            for (Reservation r : c.getReservations()) {
                if (r.getStartDate().getYear() == year) {
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

        return maxEntry.getKey();
    }
}
