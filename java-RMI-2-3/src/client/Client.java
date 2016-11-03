package client;

import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.CarRentalCompanyRemote;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;
import session.SessionManagerRemote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Client extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {

    private SessionManagerRemote sessionManagerRemote = null;

    public static void main(String[] args) throws Exception {

        Client client = new Client("trips");
        client.run();
    }

    public Client(String scriptFile) {
        super(scriptFile);

        try {
            Registry registry = LocateRegistry.getRegistry();
            sessionManagerRemote = (SessionManagerRemote) registry.lookup("NameServer");

            // Let's just assume that there exist managers that add the companies to the rental agency
            ManagerSessionRemote msr = sessionManagerRemote.getManagerSession("Manager");
            msr.registerCompany((CarRentalCompanyRemote) registry.lookup("hertz"));
            msr.registerCompany((CarRentalCompanyRemote) registry.lookup("dockx"));

        } catch (Exception e) {
            System.out.println("An exception occurred in the client.");
            e.printStackTrace();
        }
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        return sessionManagerRemote.getRentalSession(name);
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        /* TODO: ? carRentalName is unused. */
        return sessionManagerRemote.getManagerSession(name);
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
