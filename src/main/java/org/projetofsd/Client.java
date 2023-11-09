package org.projetofsd;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    static final int DEFAULT_PORT = 2000;
    static final String DEFAULT_HOST = "127.0.0.1";

    //Menu
    public static int menu() {
        int selection;
        Scanner input = new Scanner(System.in);

        /***************************************************/
        System.out.println("\n************************************************************");
        System.out.println("*	           Menu Cliente                            *");
        System.out.println("************************************************************   ");
        System.out.println("*	1 - Adicionar/Remover Stock ");
        System.out.println("*	2 - Sair      ");
        System.out.print("Selecione uma opção(1-2): ");
        selection = input.nextInt();
        return selection;
    }


    public static void main(String[] args) throws IOException {

        //Pedir ip e porta do server
        if (args.length < 2) {
            System.out.println("Uso: java Client <IP_do_Servidor> <Porta_do_Servidor>");
            System.exit(1);
        }
        //introduzir ip servidor e porta do server por argumento ao executar o cliente
        String server = args[0];
        int port = Integer.parseInt(args[1]);

        //versão exercício pl
        String servidors = DEFAULT_HOST;
        int portos = DEFAULT_PORT;
        Scanner input = new Scanner(System.in);
        System.out.println("\n************************************************************");
        System.out.println("*	            Connectado com Sucesso               *");
        System.out.println("************************************************************   ");
        //Lista o stock ao iniciar o servidor
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    InetAddress serverAddress = InetAddress.getByName(server);
                    Socket socket = null;
                    socket = new Socket(serverAddress, port);



                    // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Create a java.io.PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    String request = "STOCK_REQUEST";

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
            }
        };
        // Cria um temporizador que executa a tarefa a cada 5 segundos
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 10000);


        int selection;
        do {

            selection = menu();

            switch (selection) {
                case 1:
                    // Código para a opção "2" (adicionar/remover stock)
                    System.out.print("Introduza o identificador do produto: ");
                    String productIdentifier = input.next();
                    System.out.print("Introduza a quantidade a adicionar (nªpositivo) ou a remover (nªnegativo): ");
                    int quantityChange = input.nextInt();

                    try {
                        InetAddress serverAddress = InetAddress.getByName(server);
                        Socket socket = null;
                        socket = new Socket(serverAddress, port);

                        // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        // Create a java.io.PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                        String request = "STOCK_UPDATE" + " " + productIdentifier + " " + quantityChange;

                        // write the request into the Socket
                        out.println(request);

                        // Read the server response
                        String response = in.readLine();
                        System.out.println(response);

                        // Close the socket
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Erro ao comunicar com o servidor: " + e);
                        System.exit(1);
                    }
                    break;
                case 2:
                    System.out.println("A sair do programa.");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (selection != 2);
        timer.cancel();
    }
}



