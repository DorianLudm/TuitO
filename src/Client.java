import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client{
    private String pseudo;
    private List<Message> messages;

    public Client(String pseudo){
        this.pseudo = pseudo;
        this.messages = new ArrayList<Message>();
    }

    public String getPseudo(){
        return this.pseudo;
    }

    public List<Message> getMessages(){
        return this.messages;
    }

    public static void main(String[] args) {
        Scanner scannerPseudo = new Scanner(System.in);
        System.out.print("Entrez votre pseudo: ");
        String pseudo = scannerPseudo.nextLine();
        Client client = new Client(pseudo);

        Socket socketClient = null;
        PrintWriter writer = null;
        Scanner scanner = new Scanner(System.in);
        final BufferedReader[] readerContainer = new BufferedReader[1];
    
        try {
            socketClient = new Socket("localhost", 8080);
            readerContainer[0] = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            writer = new PrintWriter(socketClient.getOutputStream(), true);
    
            // Créer un thread secondaire pour lire les messages du serveur
            new Thread(() -> {
                try {
                    String message;
                    while ((message = readerContainer[0].readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                }
            }).start();
    
            // Thread d'envoi de message vers le serveur
            System.out.print("Enter a message to send (or 'quit' to exit): \n");
            while (true) {
                String input = scanner.nextLine();

                if ("quit".equalsIgnoreCase(input)) {
                    break;
                }

                Message message = new Message(input, client);
                System.out.print("\033[1A"); // Move up
                System.out.print("\033[2K"); // Erase line content
                writer.println(message.toString());
                System.out.println("You sent: " + message.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (readerContainer[0] != null) readerContainer[0].close();
                if (writer != null) writer.close();
                if (socketClient != null) socketClient.close();
                if (scanner != null) scanner.close();
                if (scannerPseudo != null) scannerPseudo.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}