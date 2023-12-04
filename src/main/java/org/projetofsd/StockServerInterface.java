package org.projetofsd;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.*;

public interface StockServerInterface extends Remote {
    void saveStockCSVRMI(String filename) throws RemoteException;
    void subscribe(SecureDirectNotificationInterface directNotification) throws RemoteException;
    int updateStockRMI(String productIdentifier, int quantityChange) throws RemoteException;
    String stock_request() throws RemoteException;
    PublicKey getPubKey() throws NoSuchAlgorithmException,InvalidKeyException, RemoteException;
    String getStockWithSignature() throws RemoteException;
    String updateStockWithSignature(String id, int qty) throws RemoteException;
}
