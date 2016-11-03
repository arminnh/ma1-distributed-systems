package session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements SessionManagerRemote {

    private static Map<String, ReservationSession> reservationSessionMap = new HashMap<>();
    private static Map<String, ManagerSession> managerSessionMap = new HashMap<>();

    @Override
    public ReservationSessionRemote getRentalSession(String name) {
        if (!reservationSessionMap.containsKey(name)) {
            reservationSessionMap.put(name, new ReservationSession());
        }
        return reservationSessionMap.get(name);
    }

    @Override
    public ManagerSessionRemote getManagerSession(String name) {
        if (!managerSessionMap.containsKey(name)) {
            managerSessionMap.put(name, new ManagerSession());
        }
        return managerSessionMap.get(name);
    }

    @Override
    public void removeSession() {
        //todo
    }
}
