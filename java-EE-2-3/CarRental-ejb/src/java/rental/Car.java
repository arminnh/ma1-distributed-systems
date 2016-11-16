package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Car implements Serializable{
    
    @Id @GeneratedValue
    private int id;
    
    @ManyToOne(cascade=PERSIST)
    private CarType type;
    
    @OneToMany(cascade=REMOVE)
    private Set<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(CarType type) {
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }
    
    public Car() {}

    /******
     * ID *
     ******/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
        return type;
    }
    
    public void setType(CarType type) {
        this.type = type;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }
}