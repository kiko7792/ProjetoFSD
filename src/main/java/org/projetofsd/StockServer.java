package org.projetofsd;
import org.projetofsd.Stock;

import java.net.*;
import java.io.*;

public class StockServer {
    static int DEFAULT_PORT=2001;

    public static void main(String[] args) throws IOException {
        int port=DEFAULT_PORT;
        Stock stock = new Stock();

        ServerSocket servidor = null;

// Create a server socket, bound to the specified port: API java.net.ServerSocket
        ServerSocket welcomeSocket = new ServerSocket(port);

        System.out.println("Servidor a' espera de ligacoes no porto " + port);

        while(true) {
            try {

// Listen for a connection to be made to the socket and accepts it: API java.net.ServerSocket

                Socket socketPedido = welcomeSocket.accept();

// Start a GetPresencesRequestHandler thread
                GetStockRequestHandler GPRH = new GetStockRequestHandler(socketPedido, stock);
                GPRH.start();


            } catch (IOException e) {
                System.out.println("Erro na execucao do servidor: "+e);
                System.exit(1);
            }

        }
    }
}
