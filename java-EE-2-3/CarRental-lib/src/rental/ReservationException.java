package rental;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ReservationException extends Exception {

    public ReservationException(String string) {
        super(string);
    }
    
    public ReservationException(Throwable t) {
        super(t);
    }
}