package org.projetofsd;
import java.net.*;
import java.io.*;
import java.util.*;
public class Client {
    static final int DEFAULT_PORT = 2001;
    static final String DEFAULT_HOST = "127.0.0.1";

    public static int menu() {

        int selection;
        Scanner input = new Scanner(System.in);

        /***************************************************/
        System.out.println("\n************************************************************");
        System.out.println("*	           Menu Cliente                            *");
        System.out.println("************************************************************   ");
        System.out.println("*	1 - Listar Stock ");
        System.out.println("*	2 - Adicionar Stock        ");
        System.out.println("*	3 - Retirar Stock        ");
        System.out.println("*	4 - Quit                                                ");
        System.out.print("?-> ");
        selection = input.nextInt();
        return selection;
    }

    public static void main(String[] args) throws IOException {
        String servidor = DEFAULT_HOST;
        int porto = DEFAULT_PORT;
        Scanner input = new Scanner(System.in);
        String ipAddress;
        int porta;
        System.out.println("\n************************************************************");
        System.out.println("*	            Autenticação                           *");
        System.out.println("************************************************************   ");

        do {
            System.out.println("Introduza o endereço IP:");
            ipAddress = input.nextLine();
            System.out.println("Introduza a porta:");
            porta = input.nextInt();
            input.nextLine();
        } while (!ipAddress.equals("127.0.0.1") && porta != 2001);

        // Create a representation of the IP address of the Server: API java.net.InetAddress
        InetAddress serverAddress = InetAddress.getByName("localhost");

        Socket socket = new Socket(serverAddress, porto);
        menu();
        try {
            // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create a java.io.PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request = "get" + " " +ipAddress + " " + porta;

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

