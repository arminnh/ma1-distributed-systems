package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rental.*;

public class Client extends AbstractTestBooking {

	public static CarRentalCompanyInterface crci;

	/********
	 * MAIN *
	 ********/
	
	public static void main(String[] args) throws Exception {
		
		String carRentalCompanyName = "Hertz";

		System.setSecurityManager(null);

		try {
			Registry registry = LocateRegistry.getRegistry();
			crci = (CarRentalCompanyInterface) registry.lookup(carRentalCompanyName);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}


		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName);
		client.run();
	}
	
	/***************
	 * CONSTRUCTOR *
	 ***************/
	
	public Client(String scriptFile, String carRentalCompanyName) {
		super(scriptFile);
	}
	
	/**
	 * Check which car types are available in the given period
	 * and print this list of car types.
	 *
	 * @param 	start
	 * 			start time of the period
	 * @param 	end
	 * 			end time of the period
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
		try {
			crci.getAvailableCarTypes(start, end);
		} catch (RemoteException e) {
            e.printStackTrace();
        }
	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param	clientName 
	 * 			name of the client 
	 * @param 	start 
	 * 			start time for the quote
	 * @param 	end 
	 * 			end time for the quote
	 * @param 	carType 
	 * 			type of car to be reserved
	 * @param 	region
	 * 			region in which car must be available
	 * @return	the newly created quote
	 *  
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end, String carType, String region) throws Exception {
		Quote q = null;

		try {
			ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
			q = crci.createQuote(constraints, clientName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return q;
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param 	quote 
	 * 			the quote to be confirmed
	 * @return	the final reservation of a car
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		Reservation r = null;

		try {
			r = crci.confirmQuote(quote);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return r;
	}
	
	/**
	 * Get all reservations made by the given client.
	 *
	 * @param 	clientName
	 * 			name of the client
	 * @return	the list of reservations of the given client
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {
		List<Reservation> reservationList = new ArrayList<>();

		try {
			reservationList.addAll(crci.getReservationsByRenter(clientName));
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		for (Reservation res : reservationList) {
            System.out.printf(String.format("CarType: %s CarID: %d Reservation from: %s to %s Price: %.2f",
                                            res.getCarType(), res.getCarId(), res.getStartDate().toString(),
                                            res.getEndDate().toString(), res.getRentalPrice()));
        }

		return reservationList;
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param 	carType 
	 * 			name of the car type
	 * @return 	number of reservations for the given car type
	 * 
	 * @throws 	Exception
	 * 			if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {
		int number = 0;

		try {
			number = crci.getNumberOfReservationsForCarType(carType);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return number;
	}
}