package org.projetofsd;

public class ClientAppRMI {
    public static void main(String[] args) {
        ClientRMI client;

        client = new ClientRMI(args);
        client.putPresence();
    }

}
