package session;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import rentalAgency.RentalAgency;
import rental.CarRentalCompanyRemote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public class ReservationSession extends UnicastRemoteObject implements ReservationSessionRemote {

    List<Quote> quotes = new ArrayList<Quote>();
    private RentalAgency agency;

    public ReservationSession(RentalAgency agency) throws RemoteException {
        this.agency = agency;
    }
    
    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(this.agency.getRentals().keySet());
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public void createQuote(String renter, ReservationConstraints rc) throws ReservationException, RemoteException {
        CarRentalCompanyRemote crc = null;
        Quote q = null;

        for (CarRentalCompanyRemote c : this.agency.getRentals().values()) {
            if (c.hasRegion(rc.getRegion())) {
                crc = c;
                try {
                    q = crc.createQuote(rc, renter);
                    break;
                } catch (ReservationException e) {
                }
            }
        }

        if (crc != null && q != null) {
            quotes.add(q);
        } else {
            throw new ReservationException("No companies in region " + rc.getRegion() + " that satisfy the constraints.");
        }
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException, RemoteException {
        List<Reservation> reservations = new ArrayList<Reservation>();

        try {

            for (Quote q : quotes) {
                CarRentalCompanyRemote crc = this.agency.getRental(q.getRentalCompany());
                reservations.add(crc.confirmQuote(q));
            }

        } catch (ReservationException e) {

            System.out.println(e.toString());

            for (Reservation r : reservations) {
                CarRentalCompanyRemote crc = this.agency.getRental(r.getRentalCompany());
                crc.cancelReservation(r);
            }

            throw new ReservationException("A Reservation could not be finalized.");

        }

        // Assume quotes should be cleared.
        quotes.clear();

        return reservations;
    }

    @Override
    public Set<CarType> checkForAvailableCarTypes(Date start, Date end) throws RemoteException {
        Set<CarType> availableCars = new HashSet<CarType>();

        for (CarRentalCompanyRemote crc : this.agency.getRentals().values()) {
            availableCars.addAll(crc.getAvailableCarTypes(start, end));
        }

        return availableCars;
    }

    @Override
    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        List<CarType> cars = new ArrayList<>();

        for (CarRentalCompanyRemote crc : this.agency.getRentals().values()) {
            if (crc.hasRegion(region)) {
                cars.addAll(crc.getAvailableCarTypes(start, end));
            }
        }

        CarType cheapest = new CarType("temp", 0, 0, Double.MAX_VALUE, false);

        for (CarType c : cars) {
            if (c.getRentalPricePerDay() < cheapest.getRentalPricePerDay()) {
                cheapest = c;
            }
        }

        return cheapest.getName();
    }

}
