package org.projetofsd;

import java.net.*;
import java.io.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.*;
import java.util.Base64;
import java.util.StringTokenizer;
import java.lang.SecurityManager;

public class StockServer implements Remote {
    static int DEFAULT_PORTSocket = 2000;
    static int DEFAULT_PORTRMI = 1099;
    int portRMI;
    int portSocket;


    private void bindRMI(StockServerInterface stockServer) throws RemoteException {

        try {
            System.out.println("Server: "+stockServer.getPubKey());
            LocateRegistry.createRegistry(portRMI);

            LocateRegistry.getRegistry("127.0.0.1",portRMI).rebind(SERVICE_NAME, stockServer);

            System.out.println("Registry created");
        } catch (RemoteException e) {
            System.out.println(e + "Registry not found");
        }
    }



    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        if (args.length < 2) {
            System.out.println("Uso: java StockServer <Porta Socket> <Porta RMI>");
            System.exit(1);
        }

        int portSocket = Integer.parseInt(args[0]);
        int portRMI = Integer.parseInt(args[1]);

        StockServerInterface stockServer = new StockServerImpl();
        StockServer server = new StockServer();
        server.portSocket = portSocket;
        server.portRMI = portRMI;

        Stock stock = new Stock();

        ServerSocket serverSocket = null;

// Create a server socket, bound to the specified port: API java.net.ServerSocket
        serverSocket = new ServerSocket(portSocket);

        System.out.println("Servidor à espera de ligações no porto " + portSocket+"(Sockets)"+ " & "+ portRMI + "(RMI)");

        server.bindRMI(stockServer);


        while (true) {
            try {

// Listen for a connection to be made to the socket and accepts it: API java.net.ServerSocket
                Socket connection = serverSocket.accept();


// Start a GetStockRequestRequestHandler and UpdateStockHandler thread

                PrintWriter out = new PrintWriter(connection.getOutputStream());
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
                    } else if (request.equals("GET_PUBKEY")){
                        out.println(stockServer.getPublicKey());
                        out.flush();
                    }
                }


            } catch (IOException e) {
                System.out.println("STOCK_ERROR: " + e);
                System.exit(1);
            }
        }

    }
    String SERVICE_NAME="/StockServer";

}