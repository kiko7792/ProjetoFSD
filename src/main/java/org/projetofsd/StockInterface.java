package org.projetofsd;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;


public interface StockInterface extends Remote {

    public Hashtable<String, StockInfo> getStock() throws RemoteException;
}
