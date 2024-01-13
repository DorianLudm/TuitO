import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.*;
import java.sql.SQLException;

public class Server{
    private List<ClientHandler> clients;

    public Server(){
        clients = new ArrayList<ClientHandler>();
    }

    //Envoie du message à tout les utilisateurs connectés
    public void broadcast(String msg){
        for(ClientHandler liaisonClient : this.clients){
            liaisonClient.broadcast(msg);
        }
    }

    //Envoie du message à tout les utilisateurs connectés sauf le client précisé en paramètre
    public void broadcastFollower(Message msg){
        msg.uploadBD(); //Not implemented yet
        Utilisateur sender = msg.getSender().getUser();
        for(Utilisateur follower : sender.getFollowers()){
            for(ClientHandler liaisonClient : this.clients){
                if(liaisonClient.getClient().getUser().equals(follower)){
                    liaisonClient.broadcast(msg.toString());
                }
            }
        }
    }

    //Fermeture de la connexion avec le client
    public void close(ClientHandler client){
        this.clients.remove(client);
    }

    public static void main(String[] args) {
        int port = 8080;
        Server server = new Server();
        ServerSocket socketServeur = null;
        ConnexionBD connexionBD = null;
        DatabaseManager dbm = null;

        //Connection to DB
        try{
            connexionBD = new ConnexionBD();
            connexionBD.connecter();
            dbm = new DatabaseManager(connexionBD);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        //Test to remove later
        try{
            Utilisateur user = dbm.loginAccount("Pixa", "Pixa253lulu");
            System.out.println(user.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        

        //Server
        if(dbm != null){
            try {
            socketServeur = new ServerSocket(port);
    
            while (true) {
                // Création d'un ClientHandler pour chaque nouvelle connexion
                Socket socketClient = socketServeur.accept();
                String addrClient = socketClient.getRemoteSocketAddress().toString();
                ClientHandler client = new ClientHandler(new Client(), socketClient, server);
                server.clients.add(client);
                client.start();
    
                // Envoie d'un message de bienvenue à tout les utilisateurs connectés
                server.broadcast("Nouvelle connexion de " + addrClient);
            }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    if (socketServeur != null) socketServeur.close();
                } catch (IOException e) {
                    System.out.println("Error closing server socket: " + e.getMessage());
                }
            }
        }
    }
}