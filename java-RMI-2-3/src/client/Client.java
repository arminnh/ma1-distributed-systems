package client;

import rentalAgency.RentalAgencyRemote;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.CarRentalCompanyRemote;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Client extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {

    private RentalAgencyRemote rentalAgency = null;

    public static void main(String[] args) throws Exception {
        Client client = new Client("trips");
        client.run();
        client.clearSessions();
    }

    public Client(String scriptFile) {
        super(scriptFile);

        try {
            Registry registry = LocateRegistry.getRegistry();
            this.rentalAgency = (RentalAgencyRemote) registry.lookup("RentalAgency");

            // Let's just assume that there exist managers that add the companies to the rental agency
            ManagerSessionRemote msr = this.rentalAgency.getManagerSession();
            msr.registerCompany((CarRentalCompanyRemote) registry.lookup("Hertz"));
            msr.registerCompany((CarRentalCompanyRemote) registry.lookup("Dockx"));

        } catch (Exception e) {
            System.out.println("An exception occurred in the client.");
            e.printStackTrace();
        }
    }

    private void clearSessions() throws Exception {
        for (String name : this.sessions.keySet()) {
            this.rentalAgency.removeReservationSession(name);
        }
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        return this.rentalAgency.getRentalSession(name);
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        /* TODO: ? carRentalName is unused. */
        return this.rentalAgency.getManagerSession();
    }

    /*
    * Methods that allow a client to make reservations.
     */
    @Override
    protected void checkForAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        for (CarType c : session.checkForAvailableCarTypes(start, end)) {
            System.out.println(c.toString());
        }
    }

    @Override
    protected void addQuoteToSession(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        ReservationConstraints rc = new ReservationConstraints(start, end, carType, region);
        session.createQuote(name, rc);
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote reservationSessionRemote, Date start, Date end, String region) throws Exception {
        return reservationSessionRemote.getCheapestCarType(start, end, region);
    }

    /*
    * Methods the manager can use to get general info.
    */
    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.bestCustomer();
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCarType(carRentalName, carType);
    }
}
