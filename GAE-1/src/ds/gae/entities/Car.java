package ds.gae.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Car {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	
    private int id;
	
    /*
     * The unowned annotation solves this error:
     * Attempt to assign child with key "CarType(4890627720347648)" to parent with key "Car(no-id-yet)". 
     * Parent keys are immutable. Basically it's already a child of CarRentalCompany (i think) and therefore
     * cannot be asigned car as a parent, thus it is an "unowned" relationship? -> TODO: verify this
     * 
     * GAE docs say:
     * "A relationship between persistent objects can be described as owned, 
     * where one of the objects cannot exist without the other, or unowned, 
     * where both objects can exist independently of their relationship with one another"
     * --> This relationship than seems like it is unowned, as the cartype can exist without the 
     * 	   car: e.g. when other cars of this type exist.
     */
    @Unowned
    @ManyToOne(cascade=CascadeType.PERSIST)
    @Basic
	private CarType type;

	@OneToMany(cascade=CascadeType.PERSIST)
    @Basic
    private Set<Reservation> reservations = new HashSet<Reservation>();

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }
    
    public Car() { 
        this.reservations = new HashSet<Reservation>();
    }
    
    /******
     * ID *
     ******/
    
    public int getId() {
    	return id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
        return type;
    }

    /****************
     * RESERVATIONS *
     ****************/
    
    public Set<Reservation> getReservations() {
    	return reservations;
    }
    
    public void getReservations(Set<Reservation> res) {
    	this.reservations = res;
    }

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        if (reservations == null) {
        	return true;
        }
        
        for (Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
    	// FIXME: why is this always null?
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
}