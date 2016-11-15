package rental;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Reservation extends Quote {

    @Id
    private Long id;

    private int carId;
        
    /***************
     * CONSTRUCTOR *
     ***************/

    public Reservation(Quote quote, int carId) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    		quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }
    
    public Reservation() {}
    
    /******
     * ID *
     ******/
    
    public int getCarId() {
    	return carId;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }	

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}