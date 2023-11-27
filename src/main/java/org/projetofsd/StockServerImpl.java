package org.projetofsd;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.security.*;



public class StockServerImpl extends UnicastRemoteObject implements StockServerInterface{
    private final List<DirectNotificationInterface> subscribers;
    private KeyPair keyPair;
    private PublicKey clientPublicKey;
    private PrivateKey privateKey;
    public PublicKey getClientPublicKey(){
        return clientPublicKey;
    }
    public StockServerImpl() throws RemoteException, NoSuchAlgorithmException {
        subscribers = new ArrayList<>();
        keyPairGenerator();
    }

    public void keyPairGenerator() throws NoSuchAlgorithmException{
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(2048);
        keyPair = keyGenerator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        PublicKey serverPublicKey = keyPair.getPublic();

        clientPublicKey = serverPublicKey;
    }

    @Override
    public PublicKey getPubKey() throws RemoteException {
        return keyPair.getPublic();
    }
    @Override
    public PrivateKey getPrivKey() throws RemoteException {
        return keyPair.getPrivate();
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String getPrivateKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
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
    public void subscribe(DirectNotificationInterface directNotification) throws RemoteException {
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
        privateKey = privateKey;
        Stock stock = new Stock();
        stock.readStockCSV("Stock.csv");
        String signedMessage = signMessage("STOCK_REQUEST"); // Sign the message

        StringBuilder stockDetails = new StringBuilder();
        stockDetails.append("Lista de Itens no Stock:\n");

        for (Stock.StockInfo stockInfo : Stock.presentStock.values()) {
            stockDetails.append("Nome: ").append(stockInfo.getName()).append(", Identificador: ").append(stockInfo.getIdentifier()).append(", Quantidade: ").append(stockInfo.getQuantity()).append("\n");
        }

        return "SIGNED_MESSAGE:" + signedMessage + "\nPUBLIC_KEY:" + getPublicKey() + "\n" + stockDetails.toString() + "Private key" + getPrivateKey();

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

    @Override
    public String stock_update_signed(String message, String signature) throws RemoteException {
        DirectNotificationInterface directNotificationInterface = new DirectNotificationImpl();
        return directNotificationInterface.stockUpdatedSigned(message + signature);
    }

    private void notifySubscribers(String message) throws RemoteException {
        for (DirectNotificationInterface directNotification : subscribers) {
            directNotification.notifyStockUpdate(message);
        }
    }

    private String signMessage(String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }


}
