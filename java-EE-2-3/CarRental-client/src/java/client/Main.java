package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main("trips");

        ManagerSessionRemote ms = main.getNewManagerSession("main", "carRentalName? u wot m8???? it says 'uses the management interface to load the car rental companies'");
        main.loadRental("dockx.csv", ms);
        main.loadRental("hertz.csv", ms);

        main.run();
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        return (CarRentalSessionRemote) (new InitialContext()).lookup(CarRentalSessionRemote.class.getName());
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        // TODO: ask assistant what's up with the carRentalName here
        return (ManagerSessionRemote) (new InitialContext()).lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        // void function, just need to print stuff? or return exception when something is wrong?
        System.out.println("Available car types:");
        for (CarType c : session.getAvailableCarTypes(start, end)) {
            System.out.println("\t\t" + c.toString());
        }
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String carRenter, Date start, Date end, String carType, String region) throws Exception {
        session.addQuote(carRenter, carType, region, start, end);
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String carRenter) throws Exception {
        return session.confirmQuotes(carRenter);
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
        return session.getCheapestCarType(region, start, end);
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservationsForCarType(carRentalName, carType);
    }

    public void loadRental(String datafile, ManagerSessionRemote ms) {
        try {
            CrcData data = loadData(datafile, ms);
            
            ms.createCarRentalCompany(data.name);
            ms.addRegions(data.name, data.regions);
            
            for (String id : data.carTypeIDs ) {
                ms.addCarType(data.name, id);
            }
            
            for (Long i : data.carIDs ) {
                ms.addCar(data.name, i);
            }
            
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, datafile});
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public CrcData loadData(String datafile, ManagerSessionRemote ms) throws NumberFormatException, IOException {
        CrcData out = new CrcData();
        StringTokenizer csvReader;
        
        BufferedReader in = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(datafile)));
        
        try {
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    
                    //create new car type from first 5 fields
                    String typeID = ms.createCarType(csvReader.nextToken(),
                                                     Integer.parseInt(csvReader.nextToken()),
                                                     Float.parseFloat(csvReader.nextToken()),
                                                     Double.parseDouble(csvReader.nextToken()),
                                                     Boolean.parseBoolean(csvReader.nextToken()));
                    out.carTypeIDs.add(typeID);
                    
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        Long carID = ms.createCar(typeID);
                        out.carIDs.add(carID);
                    }        
                }
            } 
        } finally {
            in.close();
        }

        return out;
    }
    
    static class CrcData {
        public String name;
        public List<String> carTypeIDs = new LinkedList<String>();
        public List<Long> carIDs = new LinkedList<Long>();
        public List<String> regions =  new LinkedList<String>();
    }
}