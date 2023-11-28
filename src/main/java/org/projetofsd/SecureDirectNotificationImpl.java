package org.projetofsd;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SecureDirectNotificationImpl extends UnicastRemoteObject implements SecureDirectNotificationInterface {

    public SecureDirectNotificationImpl() throws RemoteException{

    }
    @Override
    public void notifyStockUpdate(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public String stockUpdatedSigned(String message, String signatureString) throws RemoteException {
        System.out.println("----Stock updated by user----\n"+message+"\nAssinatura:"+signatureString+"\n");
        return message+"."+signatureString;
    }

}
