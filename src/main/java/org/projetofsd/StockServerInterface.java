package org.projetofsd;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface StockServerInterface extends Remote {
    void saveStockCSVRMI(String filename) throws RemoteException;
    void readStockCSVRMI(String filename) throws RemoteException;
    void subscribe(DirectNotificationInterface directNotification) throws RemoteException;
    int updateStockRMI(String productIdentifier, int quantityChange) throws RemoteException;
    String stock_request() throws RemoteException;
    String stock_update(String id, int qtd) throws RemoteException;
    PublicKey getPubKey() throws RemoteException;

    PrivateKey getPrivKey() throws RemoteException;

    String getPublicKey() throws RemoteException;

    String getPrivateKey() throws RemoteException;
}
