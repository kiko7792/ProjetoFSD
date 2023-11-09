package org.projetofsd;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;


public interface StockInterface extends Remote {

    Hashtable<String, Stock.StockInfo> getStockRMI() throws RemoteException;

}
