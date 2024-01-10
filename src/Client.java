import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client{
    private Utilisateur user;
    private List<Message> messages;

    public Client(){
        this.messages = new ArrayList<Message>();
    }

    public Client(Utilisateur user){
        this.user = user;
        this.messages = new ArrayList<Message>();
    }

    public void setUser(Utilisateur user){
        this.user = user;
    }

    public String getPseudo(){
        return this.user.getPseudo();
    }

    public List<Message> getMessages(){
        return this.messages;
    }

    public static void main(String[] args) {
        Socket socketClient = null;
        PrintWriter writer = null;
        Scanner scanner = new Scanner(System.in);
        final BufferedReader[] readerContainer = new BufferedReader[1];

        // SYSTEME DE CONNEXION (A changer + lié BD etc)
        boolean isConnected = false;
        while(!isConnected){
            try{
                
            }
            catch(FalseLoginException e){}
        }
        //
    
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