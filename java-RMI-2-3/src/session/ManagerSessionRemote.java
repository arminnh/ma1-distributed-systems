/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import rental.CarRentalCompanyRemote;
import rental.CarType;

public interface ManagerSessionRemote extends Remote {

    int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException;

    Set<String> bestCustomer() throws RemoteException;

    CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException;

    void registerCompany(CarRentalCompanyRemote remote) throws RemoteException;

    void unregisterCompany(CarRentalCompanyRemote remote) throws RemoteException;
}
