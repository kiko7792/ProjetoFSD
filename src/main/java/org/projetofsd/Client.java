package org.projetofsd;

import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.*;
import java.util.*;

public class Client {
    static final int DEFAULT_PORT = 2000;
    static final String DEFAULT_HOST = "127.0.0.1";
    static PublicKey serverPublicKey;


    //Menu
    public static int menu() {
        int selection;
        Scanner input = new Scanner(System.in);

        /***************************************************/
        System.out.println("\n************************************************************");
        System.out.println("*	           Menu Cliente                            *");
        System.out.println("************************************************************   ");
        System.out.println("*	1 - Listar Stock ");
        System.out.println("*	2 - Adicionar/Remover Stock ");
        System.out.println("*	3 - Sair      ");
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
        System.out.println("*	             Conectado com Sucesso                *");
        System.out.println("************************************************************   ");

        try {
            // Código para se conectar ao servidor e obter a chave pública
            InetAddress serverAddress = InetAddress.getByName(server);
            Socket socket = new Socket(serverAddress, port);

            // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create a java.io.PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Enviar a mensagem "GET_PUBKEY" para o servidor
            out.println("GET_PUBKEY");

            // Receber a chave pública do servidor
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            serverPublicKey = (PublicKey) objectInputStream.readObject();
            System.out.println(serverPublicKey);

            // Agora você tem a chave pública do servidor (serverPublicKey) para usar na verificação da assinatura

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao comunicar com o servidor: " + e);
            System.exit(1);
        }




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

        requestServerPublicKey(server,port);

        int selection;
        do {

            selection = menu();

            switch (selection) {
                case 1:
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

                        StringBuilder sb = new StringBuilder();
                        int c;
                        while ((c = in.read()) != -1) {
                            sb.append((char)c);
                        }
                        String stockWithSignature = sb.toString().trim();

                        String[] parts = stockWithSignature.split("\nSIGNATURE: ");
                        if(parts.length == 2){
                            String stock = parts[0];
                            String receivedSignature = parts[1];

                            Signature signature = Signature.getInstance("SHA256withRSA");
                            signature.initVerify(serverPublicKey);

                            byte[] stockBytes = stock.getBytes();
                            signature.update(stockBytes);

                            byte[] receivedSignatureBytes =  Base64.getDecoder().decode(receivedSignature);

                            if(signature.verify(receivedSignatureBytes)){
                                System.out.println("Assinatura válida!");
                                System.out.println("Stock:\n"+stock);
                                System.out.println("\nAssinatura: "+receivedSignature);
                            }else{
                                System.out.println("Assinatura digital inválida!");
                            }
                        }else{
                            System.out.println("Formato de resposta inválido.");
                        }



                        /*
                        // Read the server response - read the data until null
                        StringBuilder sb = new StringBuilder();
                        int c;
                        while ((c = in.read()) != -1) {
                            sb.append((char)c);
                        }
                        String signedStockResponse = sb.toString().trim();


                        // Extrair a mensagem e a assinatura
                        String[] parts = signedStockResponse.split("\nSIGNATURE: ");
                        if(parts.length == 2) {
                            String stock = parts[0];
                            String signature = parts[1];

                            System.out.println(signature);

                            System.out.println(serverPublicKey);

                         */

                        /*

                        Signature signature = Signature.getInstance("SHA256withRSA");
                        signature.initVerify(serverPublicKey);
                        byte[] data = stock.getBytes();
                        signature.update(data);

                        if (signature.verify(digitalSignature)) {
                            // A assinatura é válida
                            System.out.println("Assinatura digital verificada com sucesso.");
                            // Lógica para processar o stock
                            System.out.println("Stock: " + stock + "\nAssinatura: "+digitalSignature);
                        } else {
                            // A assinatura é inválida
                            System.out.println("Assinatura digital inválida.");
                        }

                        */


                        // Para encerrar a thread
                        socket.close();

                    } catch (IOException e) {
                        System.out.println("Erro ao comunicar com o servidor: " + e);
                        System.exit(1);
                    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 2:
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
                case 3:
                    System.out.println("A sair do programa.");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (selection != 3);
        timer.cancel();

    }

    public static boolean verifySignature(String message, String signature, PublicKey publicKey) {
        System.out.println("Mensagem: \n"+message);
        System.out.println("Signature: "+signature);
        System.out.println("PublicKey: "+publicKey);

        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes());

            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            return sign.verify(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void requestServerPublicKey(String server, int port) {
        try {
            InetAddress serverAddress = InetAddress.getByName(server);
            Socket socket = new Socket(serverAddress, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request = "GET_PUBKEY";
            out.println(request);

            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
            serverPublicKey = (PublicKey) objIn.readObject();

            // Agora você tem a chave pública do servidor (serverPublicKey) para uso posterior

            // Feche o socket
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao obter a chave pública do servidor: " + e);
            System.exit(1);
        }
    }

}



