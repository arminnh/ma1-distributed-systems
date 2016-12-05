package ds.gae;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;

import ds.gae.EMF;
 
public class CarRentalModel {
	
	private static CarRentalModel instance;
	
	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}
	
	/**
	 * Get the rental company with the given name.
	 *
	 * @param 	name
	 * 			the name of the car rental company
	 * @return	the car rental company
	 */
	
	private CarRentalCompany getCarRentalCompany(String name) 
	{
		EntityManager em = EMF.get().createEntityManager();
		
		try {
			CarRentalCompany crc = em.find(CarRentalCompany.class, name);
			return crc;
		} finally {
			em.close();
		}
	}
		
	/**
	 * Get the car types available in the given car rental company.
	 *
	 * @param 	crcName
	 * 			the car rental company
	 * @return	The list of car types (i.e. name of car type), available
	 * 			in the given car rental company.
	 */
	public Set<String> getCarTypesNames(String crcName) {
		EntityManager em = EMF.get().createEntityManager();
		Set<String> results = new HashSet<String>();
		try {
			results.addAll(em.createQuery("SELECT CarType.name FROM CarTypes").getResultList());
		} finally {
			em.close();
		}
		return results;
	}

    /**
     * Get the names of all registered car rental companies
     *
     * @return	the list of car rental companies
     */
    public List<String> getAllRentalCompanyNames() {
		EntityManager em = EMF.get().createEntityManager();
		try {
			return em.createQuery("SELECT CRC.name FROM CarRentalCompany CRC").getResultList();
		} finally {
			em.close();
		}
    }


    /**
     * Get all registered car rental companies
     *
     * @return	the list of car rental companies
     */
    private List<CarRentalCompany> getAllCarRentalCompanies() 
    {
    	EntityManager em = EMF.get().createEntityManager();
		try {
			return em.createQuery("SELECT CRC FROM CarRentalCompany CRC").getResultList();
		} finally {
			em.close();
		}
    }
	
	/**
	 * Create a quote according to the given reservation constraints (tentative reservation).
	 * 
	 * @param	company
	 * 			name of the car renter company
	 * @param	renterName 
	 * 			name of the car renter 
	 * @param 	constraints
	 * 			reservation constraints for the quote
	 * @return	The newly created quote.
	 *  
	 * @throws ReservationException
	 * 			No car available that fits the given constraints.
	 */
    public Quote createQuote(String company, String renterName, ReservationConstraints constraints) throws ReservationException {
		// FIXED?: use persistence instead
    	
    	CarRentalCompany crc = this.getCarRentalCompany(company);
    	Quote out = null;

        if (crc != null) {
            out = crc.createQuote(constraints, renterName);
        } else {
        	throw new ReservationException("CarRentalCompany not found.");    	
        }
        
        return out;
    }
    
	/**
	 * Confirm the given quote.
	 *
	 * @param 	q
	 * 			Quote to confirm
	 * 
	 * @throws ReservationException
	 * 			Confirmation of given quote failed.	
	 */
	public Reservation confirmQuote(Quote q) throws ReservationException {
		// FIXED: use persistence instead

		CarRentalCompany crc = this.getCarRentalCompany(q.getRentalCompany());
        Reservation r = crc.confirmQuote(q);
        
        EntityManager em = EMF.get().createEntityManager();
        
        try {
        	em.persist(r);
        } finally {
        	em.close();
        }
        
        return r;
	}
	
    /**
	 * Confirm the given list of quotes
	 * 
	 * @param 	quotes 
	 * 			the quotes to confirm
	 * @return	The list of reservations, resulting from confirming all given quotes.
	 * 
	 * @throws 	ReservationException
	 * 			One of the quotes cannot be confirmed. 
	 * 			Therefore none of the given quotes is confirmed.
	 */
    public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {
    	EntityManager em = EMF.get().createEntityManager();
    	
    	try {
	    	EntityTransaction tx = em.getTransaction();
	    	tx.begin();
	    	
	    	try {
		    	List<Reservation> reservations = new ArrayList<Reservation>();
		    	
				for (Quote q : quotes) {
					Reservation r = this.confirmQuote(q);
					reservations.add(r);
				}
				
				tx.commit();
		    	return reservations;
	    	} finally {
	    		if (tx.isActive()) {
	    			tx.rollback();
	    		}
	    	}
    	} finally {
    		em.close();
    	}
    }
	
	/**
	 * Get all reservations made by the given car renter.
	 *
	 * @param 	renter
	 * 			name of the car renter
	 * @return	the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		// FIXED: use persistence instead
		EntityManager em = EMF.get().createEntityManager();
		
		try {
			return em.createQuery("SELECT R FROM Reservation R WHERE R.carRenter = :renter", Reservation.class).setParameter("renter", renter).getResultList();
		} finally {
			em.close();
		}
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param 	crcName
     * 			the given car rental company
     * @return	The list of car types in the given car rental company.
     */
    public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
		// FIXED: use persistence instead
    	
    	EntityManager em = EMF.get().createEntityManager();
    	
    	try {
    		return em.createQuery("SELECT CRC.carTypes from CarRentalCompany CRC", CarType.class).getResultList();
    	} finally {
    		em.close();
    	}
    }
	
    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A list of car IDs of cars with the given car type.
     */
    public Collection<Integer> getCarIdsByCarType(String crcName, CarType carType) {
    	Collection<Integer> out = new ArrayList<Integer>();
    	for (Car c : getCarsByCarType(crcName, carType)) {
    		out.add((int)c.getId());
    	}
    	return out;
    }
    
    /**
     * Get the amount of cars of the given car type in the given car rental company.
     *
     * @param	crcName
	 * 			name of the car rental company
     * @param 	carType
     * 			the given car type
     * @return	A number, representing the amount of cars of the given car type.
     */
    public int getAmountOfCarsByCarType(String crcName, CarType carType) {
    	return this.getCarsByCarType(crcName, carType).size();
    }

	/**
	 * Get the list of cars of the given car type in the given car rental company.
	 *
	 * @param	crcName
	 * 			name of the car rental company
	 * @param 	carType
	 * 			the given car type
	 * @return	List of cars of the given car type
	 */
	private List<Car> getCarsByCarType(String crcName, CarType carType) {				
		// FIXED?
		EntityManager em = EMF.get().createEntityManager();
		
		try {
			return em.createQuery("SELECT C FROM CarRentalCompany CRC JOIN CRC.cars C JOIN CRC.carType CT WHERE CRC.name = :company AND CT.name = :ct", Car.class)
					 .setParameter("company", crcName)
					 .setParameter("ct", carType.getName())
					 .getResultList();
		} finally {
			em.close();
		}
	}

	/**
	 * Check whether the given car renter has reservations.
	 *
	 * @param 	renter
	 * 			the car renter
	 * @return	True if the number of reservations of the given car renter is higher than 0.
	 * 			False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;		
	}	
	
	public void createCarRentalCompany(CarRentalCompany company) {
		EntityManager em = EMF.get().createEntityManager();		
		try {
			em.persist(company);
		} finally {
			em.close();
		}
	}
}