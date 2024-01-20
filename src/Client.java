import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * La classe Client représente un client de chat qui peut se connecter à un serveur de chat
 * et échanger des messages avec d'autres utilisateurs.
 */
public class Client{
    
    private Utilisateur user;
    
    /**
     * Constructeur par défaut de la classe Client.
     */
    public Client(){}

    /**
     * Constructeur de la classe Client avec initialisation de l'utilisateur.
     * @param user L'utilisateur associé au client.
     */
    public Client(Utilisateur user){
        this.user = user;
    }

    /**
     * Définit l'utilisateur associé au client.
     * @param user L'utilisateur à définir.
     */
    public void setUser(Utilisateur user){
        this.user = user;
    }

    /**
    * Obtient l'utilisateur associé au client.
     * @return L'utilisateur associé au client.
     */
    public Utilisateur getUser(){
        return this.user;
    }

    /**
     * Méthode principale du programme client. Établit une connexion avec le serveur
     * de chat et permet à l'utilisateur de se connecter ou de s'inscrire.
     * Une fois connecté, le client peut échanger des messages avec d'autres utilisateurs.
     * @param args Les arguments de la ligne de commande (non utilisés dans cette application).
     */
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
                            while(line == null){
                                line = reader.readLine();
                            }
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
                                    if ("ERROR".equals(response[0])) {
                                        System.out.println("Une erreur est survenue, veuillez ne pas utilisez de caractères spéciaux dans votre identifiant ou votre mot de passe.");
                                    }
                                    else{
                                        System.out.println("Un problème est survenu lors de la connexion au serveur.");
                                        System.out.println(Arrays.toString(response));
                                    }
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
                                if("Duplicate".equals(response[0])){
                                    System.out.println("\n--- Erreur d'inscription ---");
                                    System.out.println("Ce pseudo est déjà utilisé. Veuillez en choisir un autre.");
                                }
                                if("Split".equals(response[0])){
                                    System.out.println("\n--- Erreur d'inscription ---");
                                    System.out.println("Votre pseudo ou votre mot de passe est composé (contient un espace). Veuillez en choisir un autre.");
                                }
                                else{
                                    System.out.println("\n--- Erreur d'inscription ---");
                                    System.out.println("Un problème est survenu lors de la connexion au serveur.");
                                }
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
                        Gson gson = new Gson();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            try {
                                Message message = gson.fromJson(line, Message.class);
                                System.out.println(message.formatMessage());
                            } catch (JsonSyntaxException e){
                                if(line.contains("/QUIT")){
                                    System.out.println("Vous avez été déconnecté du serveur.");
                                    System.exit(0);
                                }
                                if(line.contains("/newline")){
                                    System.out.println("");
                                    System.out.println("Enter a message to send ('/quit' to exit or '/help' to see the available commands):");
                                
                                }
                                else{
                                    System.out.println(line);
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading from server: " + e.getMessage());
                    }
                }).start();

                System.out.println("");
                System.out.println("\n--- Bienvenue sur Tuit'O, voici ce que vous avez manqué! ---");
                writer.println("/LOADMSG&" + client.user.getId());

                // Thread d'envoi de message vers le serveur

                Thread.sleep(400);
                while (true) {
                    String input = scanner.nextLine();

                    if(input.startsWith("/")){
                        if(input.toUpperCase().contains("HELP")){
                            System.out.print("------------------------------------------ Command ------------------------------------------\n");
                            System.out.print("|                                 Enter a message to send                                   |\n");
                            System.out.print("|                         Enter /follow {username} to follow someone                        |\n");
                            System.out.print("|                       Enter /unfollow {username} to unfollow someone                      |\n");
                            System.out.print("|                         Enter /followers to see who follow you                            |\n");
                            System.out.print("|                         Enter /following to see who you follow                            |\n");
                            System.out.print("|                              Enter /like {id} to like a TUIT                              |\n");
                            System.out.print("|                            Enter /unlike {id} to unlike a TUIT                            |\n");
                            System.out.print("|   Enter /getlikes {id} or /getnblikes {id} or /nblikes {id} to see the likes of a TUIT    |\n");
                            System.out.print("|              Enter /history {x} or /historique {x} to see your x last TUIT                |\n");
                            System.out.print("|               Enter /remove {id} or /delete {id} to delete one of your TUIT               |\n");
                            System.out.print("|                          Enter /followers to see your followers                           |\n");
                            System.out.print("|                                   Enter /quit to exit                                     |\n");
                            System.out.print("---------------------------------------------------------------------------------------------\n\n");
                            System.out.print("-------------------Details-------------------\n");
                            System.out.print("| - {username} refer to a pseudo             |\n");
                            System.out.print("| - {id} refer to a TUIT id                  |\n");
                            System.out.print("---------------------------------------------|\n");
                            System.out.println("");
                            System.out.println("Enter a message to send ('/quit' to exit or '/help' to see the available commands):");
                        }
                        else{
                            String[] command = input.split(" ");
                            String commandToSend = "";
                            for (String string : command) {
                                commandToSend += string + "&";
                            }
                            writer.println(commandToSend);
                        }
                    }
                    else{
                        if(!input.trim().equals("")){
                            Message message = new Message(input, client.user);
                            writer.println(message.toString());
                            System.out.println("You sent: " + message.getMessage());
                        }
                    }
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