package rental;

import java.io.Serializable;

public class ReservationException extends Exception implements Serializable{

    public ReservationException(String string) {
        super(string);
    }
}