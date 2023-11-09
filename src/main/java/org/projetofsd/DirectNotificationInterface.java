package org.projetofsd;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DirectNotificationInterface extends Remote {
    void notifyStockUpdate(String message) throws RemoteException;

}
