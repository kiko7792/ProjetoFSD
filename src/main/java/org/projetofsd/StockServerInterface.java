package org.projetofsd;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StockServerInterface extends Remote {
    void saveStockCSVRMI(String filename) throws RemoteException;

    void readStockCSVRMI(String filename) throws RemoteException;
    void subscribe(DirectNotificationInterface directNotification) throws RemoteException;
    boolean updateStockRMI(String productIdentifier, int quantityChange) throws RemoteException;


    String stock_request() throws RemoteException;
    String stock_update(String id, int qtd) throws RemoteException;
}
