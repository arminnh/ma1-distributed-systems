package rental;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarRentalServer {

    private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.log(Level.SEVERE, "No data file found as argument, aborting...");
        } else {
            CarRentalCompany crc = loadRental(args[0]);
            try {
                Registry registry = LocateRegistry.getRegistry();
                CarRentalCompanyRemote crcr = (CarRentalCompanyRemote) UnicastRemoteObject.exportObject(crc, 0);
                registry.rebind(crc.getName(), crcr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    public static CarRentalCompany loadRental(String datafile) {
        try {
            CrcData data = loadData(datafile);
            CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
            logger.log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, datafile});
            return company;
        } catch (NumberFormatException ex) {
            logger.log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static CrcData loadData(String datafile) throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        int nextuid = 0;
        //open file from jar

        BufferedReader in = new BufferedReader(new FileReader(datafile));

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
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(nextuid++, type));
                    }
                }
            }
        } finally {
            in.close();
        }

        return out;
    }

    static class CrcData {
        public List<Car> cars = new LinkedList<Car>();
        public String name;
        public List<String> regions = new LinkedList<String>();
    }
}
