package client;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.*;

public class Main extends AbstractTestAgency<CarRentalSessionRemote, ManagerSessionRemote> {
    
    @EJB
    static CarRentalSessionRemote session;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("found rental companies: "+ session.getAllRentalCompanies());
        Main main = new Main("simpleTrips");
        try {
            main.run();
        } catch (Exception e) {
            
        }
    }

    public Main(String scriptFile) {
        super(scriptFile);
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext context = new InitialContext();
        return (CarRentalSessionRemote) context.lookup(CarRentalSessionRemote.class.getName());
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        InitialContext context = new InitialContext();
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        for(CarType c: session.checkForAvailableCarTypes(start, end)){
            System.out.println(c.toString());
        }
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        ReservationConstraints rc = new ReservationConstraints(start,end,carType,region);
        session.createQuote(name, rc);
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        CarRentalSessionRemote crs = (CarRentalSessionRemote) session;
        return crs.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumerOfReservationsForCarType(carRentalName, carType);
    }
}
