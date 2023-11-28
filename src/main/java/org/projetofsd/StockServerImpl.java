package org.projetofsd;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class StockServerImpl extends UnicastRemoteObject implements StockServerInterface{
    private final List<SecureDirectNotificationInterface> subscribers;
    static PrivateKey privKey;
    static PublicKey publicKey;

    public StockServerImpl() throws RemoteException {
        subscribers = new ArrayList<>();
    }

    public void keyPairGenerator() throws NoSuchAlgorithmException{
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        privKey = pair.getPrivate();
        publicKey = pair.getPublic();
    }

    public PrivateKey getPrivKey(){
        return privKey;
    }
    public PublicKey getPubKey(){
        return publicKey;
    }

    @Override
    public void saveStockCSVRMI(String filename) throws RemoteException {
        synchronized (this) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("Nome do Produto,Identificador,Quantidade");

                for (Stock.StockInfo stockInfo : Stock.presentStock.values()) {
                    writer.println(stockInfo.getName() + "," + stockInfo.getIdentifier() + "," + stockInfo.getQuantity());
                }

            } catch (IOException e) {
                System.err.println("Erro ao salvar o stock em CSV: " + e.getMessage());
                throw new RemoteException("Erro ao salvar o stock em CSV", e);
            }
        }
    }
    @Override
    public void readStockCSVRMI(String filename) throws RemoteException {
        synchronized (this) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                // Salta a primeira linha, que é o cabeçalho do CSV
                String linha;
                reader.readLine();

                while ((linha = reader.readLine()) != null) {
                    String[] partes = linha.split(",");
                    if (partes.length == 3) {
                        String nome = partes[0];
                        String identificador = partes[1];
                        int quantidade = Integer.parseInt(partes[2]);

                        // Atualiza o stock com os dados lidos
                        Stock.StockInfo stockInfo = new Stock.StockInfo(nome, identificador, quantidade);
                        Stock.presentStock.put(identificador, stockInfo);
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao ler o stock de CSV: " + e.getMessage());
                throw new RemoteException("Erro ao ler o stock de CSV", e);
            }
        }
    }

    @Override
    public void subscribe(SecureDirectNotificationInterface directNotification) throws RemoteException {
        subscribers.add(directNotification);
    }

    @Override
    public int updateStockRMI(String productIdentifier, int quantityChange) throws RemoteException {
        synchronized (this) {
            Stock.StockInfo stockInfo = null;

            for (Stock.StockInfo info : Stock.presentStock.values()) {
                if (info.getIdentifier().equals(productIdentifier)) {
                    stockInfo = info;
                    break;
                }
            }
            int a = 0;
            if( stockInfo != null) {
                a = stockInfo.updateQty(quantityChange);
            }

            if (stockInfo != null && a == 1) {
                saveStockCSVRMI("Stock.csv");
                return 1; // Stock updated successfully
            } else if (a == -1 || a == 0) {
                return -1; // Stock failed to update
            }
        }
        return 0; // Product not found
    }
    @Override
    public String stock_request() throws RemoteException {

        Stock stock = new Stock();
        stock.readStockCSV("Stock.csv");

        StringBuilder stockDetails = new StringBuilder();
        stockDetails.append("Lista de Itens no Stock:\n");

        for (Stock.StockInfo stockInfo : Stock.presentStock.values()) {
            stockDetails.append("Nome: ").append(stockInfo.getName()).append(", Identificador: ").append(stockInfo.getIdentifier()).append(", Quantidade: ").append(stockInfo.getQuantity()).append("\n");
        }

        return stockDetails.toString();
    }

    @Override
    public String stock_update(String id, int qty) throws RemoteException {

        int success = updateStockRMI(id, qty);

        if(success == 1){
            System.out.println("STOCK_UPDATED");
            String stock = "\nSTOCK_UPDATED\n";
            stock += stock_request();
            notifySubscribers("\nStock updated by user: \n" + stock_request());
            return stock;

        }else{
            return "STOCK_ERROR";
        }
    }

    private void notifySubscribers(String message) throws RemoteException {
        for (SecureDirectNotificationInterface directNotification : subscribers) {
            directNotification.notifyStockUpdate(message);
        }
    }
    private void notifySubscribersWithSignature(String message, String signature) throws RemoteException {
        for (SecureDirectNotificationInterface directNotification : subscribers) {
            directNotification.stockUpdatedSigned(message,signature);
        }
    }

    @Override
    public String getStockWithSignature() throws RemoteException {
        try {
            // Lógica para obter o stock (substitua com sua lógica real)
            Stock stock = new Stock();
            stock.readStockCSV("Stock.csv");

            StringBuilder stockDetails = new StringBuilder();
            stockDetails.append("Lista de Itens no Stock:\n");

            for (Stock.StockInfo stockInfo : Stock.presentStock.values()) {
                stockDetails.append("Nome: ").append(stockInfo.getName()).append(", Identificador: ").append(stockInfo.getIdentifier()).append(", Quantidade: ").append(stockInfo.getQuantity()).append("\n");
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] messageHash = digest.digest(stockDetails.toString().getBytes());

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privKey);
            signature.update(messageHash);
            byte[] digitalSignature = signature.sign();

            String signatureString = Base64.getEncoder().encodeToString(digitalSignature);


            // Devolve o stock e assinatura digital concatenados
            return stockDetails.toString()+"."+signatureString+"."+bytesToHex(messageHash);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RemoteException("Erro ao processar a assinatura digital", e);
        }
    }

    @Override
    public String updateStockWithSignature(String id, int qty) throws RemoteException{
        int success = updateStockRMI(id, qty);
        if(success == 1){
            String stock = stock_request();
            try{

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] messageHash = digest.digest(stock.getBytes());

                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initSign(privKey);
                signature.update(messageHash);
                byte[] digitalSignature = signature.sign();

                String signatureString = Base64.getEncoder().encodeToString(digitalSignature);

                notifySubscribersWithSignature(stock, signatureString);
                return stock+"."+signatureString+"."+bytesToHex(messageHash);
            }catch (NoSuchAlgorithmException e) {
                throw new RemoteException("Algoritmo de assinatura não suportado", e);
            } catch (InvalidKeyException e) {
                throw new RemoteException("Chave privada inválida para assinatura", e);
            } catch (SignatureException e) {
                throw new RemoteException("Erro ao assinar os dados", e);
            }
        }else{
            throw new RemoteException("STOCK_ERROR");
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }


}
