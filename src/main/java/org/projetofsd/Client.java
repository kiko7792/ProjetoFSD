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
        System.out.print("Selecione uma opção(1-4): ");
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
        } while (!ipAddress.equals("127.0.0.1") || porta != 2001);


        int selection;
        do {

            selection = menu();

            switch (selection) {
                case 1:
                    // O usuário selecionou a opção "1" para listar o estoque
                    try {
                        InetAddress serverAddress = InetAddress.getByName("localhost");

                        Socket socket = new Socket(serverAddress, porto);

                        // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        // Create a java.io.PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                        String request = "get";

                        // write the request into the Socket
                        out.println(request);

                        // Read the server response - read the data until null
                        String msg;
                        while ((msg = in.readLine()) != null) {
                            System.out.println(msg);
                        }
                        // Para encerrar a thread
                        socket.close();

                    } catch (IOException e) {
                        System.out.println("Erro ao comunicar com o servidor: " + e);
                        System.exit(1);
                    }
                    break;
                case 2:
                    // Código para a opção "2" (adicionar estoque)
                    // ...
                    break;
                case 3:
                    // Código para a opção "3" (retirar estoque)
                    // ...
                    break;
                case 4:
                    System.out.println("A sair do programa.");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (selection != 4); // Continua exibindo o menu até que o usuário selecione a opção "4" (sair)
    }
}



