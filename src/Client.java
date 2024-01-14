import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client{
    private Utilisateur user;

    public Client(){}

    public Client(Utilisateur user){
        this.user = user;
    }

    public void setUser(Utilisateur user){
        this.user = user;
    }

    public Utilisateur getUser(){
        return this.user;
    }

    public static void main(String[] args) {
        Client client = new Client();
        try{
            Socket socketClient = new Socket("localhost", 8080);
            PrintWriter writer = new PrintWriter(socketClient.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            // SYSTEME DE CONNEXION
            boolean isConnected = false;
            while(!isConnected){
                System.out.println("\n--- Connexion au serveur ---");
                System.out.println("Souhaitez vous vous connecter ou vous inscrire ?");
                System.out.println("1. Connexion");
                System.out.println("2. Inscription");
                System.out.println("3. Quitter");

                String choice = scanner.nextLine();
                switch(choice){
                    case "1":
                        try{
                            // Envoie des identifiants au serveur
                            writer = new PrintWriter(socketClient.getOutputStream(), true);
                            System.out.println("\n--- Connexion ---");
                            System.out.print("Identifiant : ");
                            String username = scanner.nextLine();
                            System.out.print("Mot de passe : ");
                            String password = scanner.nextLine();
                            writer.println("/LOGIN&" + username + "&" + password);
                            
                            // Réception de la réponse du serveur
                            String line = reader.readLine();
                            String[] response = line.split("&");
                            if ("True".equals(response[0])) {
                                isConnected = true;
                                System.out.println("\n--- Connexion réussie ---");
                                System.out.println("Vous êtes connecté en tant que " + response[1] + ".");
                                client.user = new Utilisateur();
                                client.user.setPseudo(response[1]);
                                client.user.setId(Integer.parseInt(response[2]));
                            } 
                            else {
                                System.out.println("\n--- Erreur de connexion ---");
                                if ("False".equals(response[0])) {
                                    System.out.println("Identifiant ou mot de passe incorrect. Veuillez réessayer.");
                                }
                                else{
                                    System.out.println("Un problème est survenu lors de la connexion au serveur.");
                                    System.out.println(Arrays.toString(response));
                                }
                            }
                        }
                        catch(Exception e){
                            System.out.println("\n--- Erreur de connexion ---");
                            System.out.println("Erreur lors de la connexion au serveur.");
                            e.printStackTrace();
                        }
                        break;
                    case "2":
                        try{
                            // Envoie des identifiants au serveur
                            writer = new PrintWriter(socketClient.getOutputStream(), true);
                            System.out.println("\n--- Inscription ---");
                            System.out.print("Identifiant : ");
                            String username = scanner.nextLine();
                            System.out.print("Mot de passe : ");
                            String password = scanner.nextLine();
                            writer.println("/REGISTER&" + username + "&" + password);
                            
                            // Réception de la réponse du serveur
                            String line = reader.readLine();
                            String[] response = line.split("&");
                            if ("True".equals(response[0])) {
                                isConnected = true;
                                System.out.println("\n--- Inscription réussie ---");
                                System.out.println("Vous êtes connecté en tant que " + response[1] + ".");
                                client.user = new Utilisateur();
                                client.user.setPseudo(response[1]);
                                client.user.setId(Integer.parseInt(response[2]));
                            } 
                            else {
                                System.out.println("\n--- Erreur d'inscription ---");
                                System.out.println("Un problème est survenu lors de la connexion au serveur.");
                            }
                        }
                        catch(Exception e){
                            System.out.println("\n--- Erreur d'inscription ---");
                            System.out.println("Un problème est survenu lors de la connexion au serveur.");
                            e.printStackTrace();
                        }
                        break;
                    case "3":
                        System.out.println("Au revoir !");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Veuillez entrer un choix valide.");
                        break;
                }
            }
            
            //
            try {
                // Créer un thread secondaire pour lire les messages du serveur
                new Thread(() -> {
                    try {
                        String message;
                        while ((message = reader.readLine()) != null) {
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

                    Message message = new Message(input, client.user);
                    writer.println(message.toString());
                    System.out.println("You sent: " + message.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (writer != null) writer.close();
                    if (socketClient != null) socketClient.close();
                    if (scanner != null) scanner.close();
                } catch (IOException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }
        }
        catch(IOException e){
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}