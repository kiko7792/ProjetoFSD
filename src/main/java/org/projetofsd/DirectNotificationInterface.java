package org.projetofsd;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DirectNotificationInterface {
    String Stock_updated(String message) throws RemoteException;
}
