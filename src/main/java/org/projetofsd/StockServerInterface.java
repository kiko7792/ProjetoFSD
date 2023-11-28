package org.projetofsd;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.*;

public interface StockServerInterface extends Remote {
    void saveStockCSVRMI(String filename) throws RemoteException;

    void readStockCSVRMI(String filename) throws RemoteException;
    void subscribe(SecureDirectNotificationInterface directNotification) throws RemoteException;
    int updateStockRMI(String productIdentifier, int quantityChange) throws RemoteException;
    String stock_request() throws RemoteException;
    String stock_update(String id, int qtd) throws RemoteException;
    void keyPairGenerator() throws NoSuchAlgorithmException, RemoteException;
    PublicKey getPubKey() throws NoSuchAlgorithmException,InvalidKeyException, RemoteException;
    PrivateKey getPrivKey() throws NoSuchAlgorithmException,InvalidKeyException,RemoteException;
    String getStockWithSignature() throws RemoteException;
    String updateStockWithSignature(String id, int qty) throws RemoteException;
}
