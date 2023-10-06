package org.projetofsd;
import java.net.*;
import java.io.*;

public class Client {
    static final int DEFAULT_PORT = 2000;
    static final String DEFAULT_HOST = "127.0.0.1";

    public static void main(String[] args) throws IOException {
        String servidor = DEFAULT_HOST;
        int porto = DEFAULT_PORT;

        if (args.length != 1) {
            System.out.println("Erro: use java presencesClient <ip>");
            System.exit(-1);
        }

        // Create a representation of the IP address of the Server: API java.net.InetAddress
        InetAddress serverAddress = InetAddress.getByName("localhost");

        Socket socket = new Socket(serverAddress, porto);

        try {
            // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create a java.io.PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request = "get" + " " + args[0];

            // write the request into the Socket
            out.println(request);

            // Read the server response - read the data until null
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
            }

            //System.out.println("Terminou a ligacao!");
        } catch (IOException e) {
            System.out.println("Erro ao comunicar com o servidor: " + e);
            System.exit(1);
        }
    }
}
