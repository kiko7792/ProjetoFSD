package org.projetofsd;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;


public interface StockInterface extends Remote {

    Hashtable<String, StockInfo> getStockRMI() throws RemoteException;

    void saveStockCSVRMI(String filename) throws RemoteException;

    void readStockCSVRMI(String filename) throws RemoteException;

    boolean updateStockRMI(String productIdentifier, int quantityChange) throws RemoteException;
}
