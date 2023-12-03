package org.projetofsd;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.Enumeration;
import java.util.*;
import java.security.*;


public class GetStockRequestHandler extends Thread implements Serializable {
    Socket socket;
    Stock stock;
    BufferedReader in;
    PrintWriter out;
    String request;
    private PrivateKey privateKey;


    public GetStockRequestHandler(Socket socket, Stock stock, String request, PrivateKey privateKey) {
        this.socket = socket;
        this.stock = stock;
        this.request = request;
        this.privateKey = privateKey;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Erro na execucao do servidor: " + e);
            System.exit(1);
        }
    }

    public void run() {
        synchronized (this) {
            try {
                System.out.println("Aceitou ligacao de cliente no endereco " + socket.getInetAddress() + " na porta " + socket.getPort());

                stock.readStockCSV("Stock.csv");
                String stock_response = "";
                String msg = request;



                StringTokenizer tokens = new StringTokenizer(msg);
                String metodo = tokens.nextToken();
                Hashtable<String, Stock.StockInfo> stockList = stock.getStock();
                if (metodo.equals("STOCK_REQUEST")) {
                    StringBuilder stock_responseBuilder = new StringBuilder();
                    for (Enumeration<String> keys = stockList.keys(); keys.hasMoreElements(); ) {
                        String key = keys.nextElement();
                        Stock.StockInfo stockInfo = stockList.get(key);

                        stock_responseBuilder.append("\nNome do Produto: ").append(stockInfo.getName())
                                .append("\nIdentificador: ").append(stockInfo.getIdentifier())
                                .append("\nQuantidade em stock: ").append(stockInfo.getQuantity())
                                .append("\n---------------------------");
                    }
                    stock_response = stock_responseBuilder.toString();
                }
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initSign(privateKey);
                byte[] stock = stock_response.getBytes();
                signature.update(stock);
                byte[] digitalSignature = signature.sign();

                System.out.println("PrivateKey no handler:" + privateKey);

                String digitalSignaturString = Base64.getEncoder().encodeToString(digitalSignature);

                String stockWithSignature = stock_response + "SIGNATURE:" + digitalSignaturString;

                out.println(stockWithSignature);


                out.flush();
                in.close();
                out.close();
                socket.close();

            } catch (IOException e) {
                System.out.println("STOCK_ERROR: " + e);
                System.exit(1);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private String signMessage(String message, PrivateKey privKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA"); // ou o algoritmo correspondente à sua chave
            signature.initSign(privKey);
            signature.update(message.getBytes());
            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }
}



