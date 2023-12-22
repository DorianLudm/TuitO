import java.io.*;
import java.net.*;
import java.nio.Buffer;
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
        // Initialisation de la connexion
        Socket socketClient = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            socketClient = new Socket("localhost", 8080);
            reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            writer = new PrintWriter(socketClient.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(e);
        }

        // Boucle de lecture des messages
        while (true) {
            try {
                String message = reader.readLine();
                System.out.println(message);

                // Send a message to the server
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter a message to send: ");
                String input = scanner.nextLine();
                writer.println(input);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}