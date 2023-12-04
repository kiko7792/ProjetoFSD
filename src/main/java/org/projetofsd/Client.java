package org.projetofsd;

import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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
            InetAddress serverAddress = InetAddress.getByName(server);
            Socket socket = new Socket(serverAddress, port);

            // Create a java.io.BufferedReader for the Socket; Use java.io.Socket.getInputStream() to obtain the Socket input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create a java.io.PrintWriter for the Socket; Use java.io.Socket.etOutputStream() to obtain the Socket output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Enviar a mensagem "GET_PUBKEY" para o servidor
            String keyRequest = "GET_PUBKEY";
            out.println(keyRequest);

            // Receber a chave pública do servidor
            String msg = in.readLine();
            if (msg != null && msg.startsWith("PUB_KEY: ")) {
                serverPublicKey = getServerPublicKey(msg.substring("PUB_KEY: ".length()));
                System.out.println("Chave pública do servidor recebida com sucesso: " + serverPublicKey);
            } else {
                System.out.println("Erro ao receber a chave pública do servidor.");
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao comunicar com o servidor: " + e);
            System.exit(1);
        }

        Client client = new Client();

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

                    StringBuilder responseBuilder = new StringBuilder();
                    String line;

                    // Ler enquanto houver dados disponíveis
                    while ((line = in.readLine()) != null) {
                        responseBuilder.append(line).append("\n");
                    }

                    String response = responseBuilder.toString().trim();

                    // Verificar se a resposta não é nula e se contém os dados
                    if (!response.isEmpty()) {
                        // Verificar a assinatura com a chave pública do servidor
                        if (response.contains("SIGNATURE:")) {
                            // Separar os dados e a assinatura
                            String[] parts = response.split("SIGNATURE:");
                            String stockData = parts[0];
                            String signature = parts[1];

                            if (client.verifySignature(stockData, signature, serverPublicKey)) {
                                System.out.println("Assinatura verificada com sucesso.");
                                System.out.println("Dados do Stock:\n" + stockData);
                            } else {
                                System.out.println("Assinatura inválida. Os dados podem ter sido modificados.");
                            }
                        } else {
                            System.out.println("Resposta do servidor não contém assinatura.");
                        }
                    } else {
                        System.out.println("Resposta do servidor vazia ou nula.");
                    }
                    // Para encerrar a thread
                    socket.close();

                } catch (IOException e) {
                    System.out.println("Erro ao comunicar com o servidor: " + e);
                    System.exit(1);
                }
            }
        };

        // Cria um temporizador que executa a tarefa a cada 10 segundos
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 10000);

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

                        StringBuilder responseBuilder = new StringBuilder();
                        String line;

                        //Ler enquanto houverem dados disponíveis
                        while ((line = in.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }

                        String response = responseBuilder.toString().trim();

                        // Verificar se a resposta não é nula e se contém dados
                        if (!response.isEmpty()) {
                            // Verificar a assinatura com a chave pública do servidor
                            if (response.contains("SIGNATURE:")) {
                                // Separar os dados e a assinatura
                                String[] parts = response.split("SIGNATURE:");
                                String stockData = parts[0];
                                String signature = parts[1];

                                if (client.verifySignature(stockData, signature, serverPublicKey)) {
                                    System.out.println("Assinatura verificada com sucesso.");
                                    System.out.println("Dados do Stock:\n" + stockData);
                                } else {
                                    System.out.println("Assinatura inválida. Os dados podem ter sido modificados.");
                                }
                            } else {
                                System.out.println("Resposta do servidor não contém assinatura.");
                            }
                        } else {
                            System.out.println("Resposta do servidor vazia ou nula.");
                        }
                        // Para encerrar a thread
                        socket.close();

                    } catch (IOException e) {
                        System.out.println("Erro ao comunicar com o servidor: " + e);
                        System.exit(1);
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
                        StringBuilder responseBuilder = new StringBuilder();
                        String line;

                        // Ler enquanto houverem dados disponíveis
                        while ((line = in.readLine()) != null) {
                            responseBuilder.append(line).append("\n");
                        }

                        String response = responseBuilder.toString().trim();

                        // Verificar se a resposta não é nula e se contém dados
                        if (!response.isEmpty()) {
                            // Verificar a assinatura com a chave pública do servidor
                            if (response.contains("SIGNATURE:")) {
                                // Separar os dados e a assinatura
                                String[] parts = response.split("SIGNATURE:");
                                String stockData = parts[0];
                                String signature = parts[1];


                                if (client.verifySignature(stockData, signature, serverPublicKey)) {
                                    System.out.println("Assinatura verificada com sucesso.");
                                    System.out.println("Dados do Stock:\n" + stockData);
                                } else {
                                    System.out.println("Assinatura inválida. Os dados podem ter sido modificados.");
                                }
                            } else {
                                System.out.println("Resposta do servidor não contém assinatura.");
                            }
                        } else {
                            System.out.println("Resposta do servidor vazia ou nula.");
                        }

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

    private boolean verifySignature(String data, String signature, PublicKey publicKey) {
        try {
            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            //System.out.println("publickey:" + publicKey);
            //System.out.println("assinatura:" + signature);
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);
            byte[] stock = data.getBytes();
            verifier.update(stock);
            boolean verified = verifier.verify(signatureBytes);

            return verified;

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

private static PublicKey getServerPublicKey(String keyString) {
    try {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        throw new RuntimeException(e);
    }
}
}



