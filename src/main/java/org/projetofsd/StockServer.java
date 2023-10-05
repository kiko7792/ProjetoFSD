package org.projetofsd;
import java.net.*;
import java.io.*;
    public class StockServer {
        static int DEFAULT_PORT=2000;

        public static void main(String[] args) throws IOException {
            int port=DEFAULT_PORT;
            Stock presences = new Stock();

            ServerSocket servidor = null;

// Create a server socket, bound to the specified port: API java.net.ServerSocket
            ServerSocket welcomeSocket = new ServerSocket(port);

            System.out.println("Servidor a' espera de ligacoes no porto " + port);

            while(true) {
                try {

// Listen for a connection to be made to the socket and accepts it: API java.net.ServerSocket

                    Socket socketPedido = welcomeSocket.accept();

// Start a GetPresencesRequestHandler thread

                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(socketPedido.getInputStream()));
                    PrintWriter out = new PrintWriter(socketPedido.getOutputStream(), true);

                    String pedido = in.readLine();
                    String resposta = pedido.toUpperCase();
                    out.println(resposta);
                    socketPedido.close();

                } catch (IOException e) {
                    System.out.println("Erro na execucao do servidor: "+e);
                    System.exit(1);
                }
            }
        }
}
