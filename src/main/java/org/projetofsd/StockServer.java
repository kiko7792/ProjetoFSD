package org.projetofsd;

import java.net.*;
import java.io.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.StringTokenizer;
import java.lang.SecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class StockServer {
    static int DEFAULT_PORT = 2000;

    public static void main(String[] args) throws IOException {

        int port = DEFAULT_PORT; // Usar o valor padrão se não for fornecido como argumento

        if (args.length >= 1) {
            port = Integer.parseInt(args[0]); // Usar o valor fornecido como argumento
        }

        Stock stock = new Stock();

        ServerSocket server = null;

// Create a server socket, bound to the specified port: API java.net.ServerSocket
        server = new ServerSocket(port);

        System.out.println("Servidor à espera de ligações no porto " + port);

        while (true) {
            try {

// Listen for a connection to be made to the socket and accepts it: API java.net.ServerSocket
                Socket connection = server.accept();


// Start a GetStockRequestRequestHandler and UpdateStoockHandler thread

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String request = in.readLine();
                String msg = request;

                StringTokenizer tokens = new StringTokenizer(msg);
                String metodo = tokens.nextToken();
                if (request != null) {
                    if (request.equals("STOCK_REQUEST")) {
                        GetStockRequestHandler gsrh = new GetStockRequestHandler(connection, stock, request);
                        gsrh.start();
                    } else if (metodo.equals("STOCK_UPDATE")) {
                        UpdateStockHandler ush = new UpdateStockHandler(connection, stock, request);
                        ush.start();
                    }
                }


            } catch (IOException e) {
                System.out.println("STOCK_ERROR: " + e);
                System.exit(1);
            }

        }
    }
    String SERVICE_NAME = "/StockServer";

    private void bindRMI (Stock stock) throws RemoteException {

        System.getProperties().put("java.security.policy", "./server.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {

        }
        try {
            LocateRegistry.getRegistry("127.0.0.1", 1099).rebind(SERVICE_NAME, (Remote) stock);
        } catch (RemoteException e) {
            System.out.println("Registry not found");
        }
    }
}
