package org.projetofsd;
import org.projetofsd.Stock;

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class StockServer {
    static int DEFAULT_PORT=2000;

    public static void main(String[] args) throws IOException {

        int port=DEFAULT_PORT;
        Stock stock = new Stock();
        boolean isOption1Selected = false; // Variável de controle

        ServerSocket servidor = null;

// Create a server socket, bound to the specified port: API java.net.ServerSocket
        servidor = new ServerSocket(port);

        System.out.println("Servidor a' espera de ligacoes no porto " + port);

        while(true) {
            try {

// Listen for a connection to be made to the socket and accepts it: API java.net.ServerSocket
                Socket ligacao = servidor.accept();




// Start a GetPresencesRequestHandler thread

                   GetStockRequestHandler gsrh = new GetStockRequestHandler(ligacao, stock);
                   gsrh.start();


                   UpdateStockHandler ush = new UpdateStockHandler(ligacao, stock);
                   ush.start();




            } catch (IOException e) {
                System.out.println("Erro na execucao do servidor: "+e);
                System.exit(1);
            }

        }
    }
}
