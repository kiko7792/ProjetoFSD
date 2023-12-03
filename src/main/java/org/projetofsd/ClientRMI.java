package org.projetofsd;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Scanner;


public class ClientRMI {

    String SERVICE_NAME="/StockServer";
    String[] args;
    PublicKey serverPubKey;

    public ClientRMI(String[] args)  {
        this.args = args;
    }
    public void putPresence() {
        if (args.length != 2) {
            System.out.println("Erro: use java ClientApp <ipClient> <ipNameServer>");
            System.exit(-1);
        }

        try {
            SecureDirectNotificationInterface directNotifications = new SecureDirectNotificationImpl();


            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));

            StockServerInterface stockServer = (StockServerInterface) registry.lookup(SERVICE_NAME);
            serverPubKey = stockServer.getPubKey();
            String pubKey = null;
            if (serverPubKey != null) {
                pubKey = Base64.getEncoder().encodeToString(serverPubKey.getEncoded());
            }

            stockServer.subscribe(directNotifications);

            System.out.println("Aceitou ligacao de cliente no endereco " + args[0] + " na porta " + args[1]);


            System.out.println("\n************************************************************");
            System.out.println("*	               Conectado com Sucesso                  *");
            System.out.println("************************************************************   ");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n************************************************************");
                System.out.println("*	                Menu Cliente RMI                      *");
                System.out.println("************************************************************   ");
                System.out.println("*	1 - Listar Stock ");
                System.out.println("*	2 - Adicionar/Remover Stock ");
                System.out.println("*	3 - Sair      ");
                System.out.print("Selecione uma opção(1-3): ");
                int escolha = scanner.nextInt();


                if (escolha == 1) {

                    String stockWithSignature = stockServer.getStockWithSignature();
                    String[] parts = stockWithSignature.split("\\.");
                    String stock = parts[0];
                    String signature = parts[1];
                    String receivedMessageHash = parts[2];
                    processStockWithSignature(stock,signature,receivedMessageHash);

                } else if (escolha == 2) {
                    // Atualizar a quantidade de um item no stock
                    String stock = stockServer.stock_request();
                    System.out.println(stock);
                    System.out.print("Introduza o identificador do produto: ");
                    String productIdentifier = scanner.next();
                    System.out.print("Introduza a quantidade a adicionar (nªpositivo) ou a remover (nªnegativo): ");
                    int quantityChange = scanner.nextInt();

                    String updatedStockWithSignature = stockServer.updateStockWithSignature(productIdentifier,quantityChange);
                    String[] parts = updatedStockWithSignature.split("\\.");
                    String updatedStock = parts[0];
                    String signature = parts[1];
                    String receivedMessageHash = parts[2];
                    processStockWithSignature(updatedStock,signature,receivedMessageHash);

                } else if (escolha == 3) {
                    // Sair do loop e encerrar o cliente
                    break;
                } else {
                    System.out.println("Escolha inválida. Tente novamente.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processStockWithSignature(String stock, String digitalSignatureString, String receivedMessageHash) {
        try {
            // Gera um número aleatório entre 0 e 1
            double randomValueForSignature = Math.random();
            double randomValueForIntegrity = Math.random();

            // Probabilidade de 10% de alterar a assinatura
            if (randomValueForSignature < 0.1) {
                digitalSignatureString = tamperSignature(digitalSignatureString);
                System.out.println("Assinatura digital alterada.");
            }
            if(randomValueForIntegrity < 0.1) {
                stock = tamperMessage(stock);
                System.out.println("Mensagem alterada.");
            }

            byte[] digitalSignature = Base64.getDecoder().decode(digitalSignatureString);

            // Verificação da assinatura digital
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(serverPubKey);
            byte[] receivedHashBytes = hexToBytes(receivedMessageHash);
            signature.update(receivedHashBytes);
            boolean verified = signature.verify(digitalSignature);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] localHash = digest.digest(stock.getBytes());

            if (verified) {
                if(MessageDigest.isEqual(receivedHashBytes,localHash)) {
                    // A assinatura é válida, processar o stock
                    System.out.println("Assinatura digital e integridade verificada com sucesso.");
                    System.out.println("Stock: " + stock);
                    System.out.println("Assinatura: " + digitalSignatureString);
                }else{
                    System.out.println("A mensagem pode ter sido adulterada.\n"+ stock);
                }
            } else {
                // A assinatura não é válida, tratar de acordo
                System.out.println("Assinatura digital inválida. O stock pode não estar correto.\n" + stock );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String tamperSignature(String originalSignature) {
        // Substituímos os dois primeiros caracteres da assinatura simulando uma alteração maliciosa
        return "XX" + originalSignature.substring(2);
    }

    private String tamperMessage(String originalMessage) {
        // Aqui, você pode, por exemplo, adicionar ou remover caracteres da mensagem
        return "TAMPERED_" + originalMessage;
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }


}
