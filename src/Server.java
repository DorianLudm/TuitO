import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.*;
import java.sql.SQLException;

public class Server{
    private List<ClientHandler> clients;
    private DatabaseManager dbm;
    private ConnexionBD connexionBD;

    public Server(){
        clients = new ArrayList<ClientHandler>();
        this.connexionBD = null;
        this.dbm = null;
    }

    //Envoie du message à tout les utilisateurs connectés
    public void broadcast(String msg){
        for(ClientHandler liaisonClient : this.clients){
            liaisonClient.broadcast(msg);
        }
    }

    //Envoie du message à tout les utilisateurs connectés sauf le client précisé en paramètre
    public void broadcastFollower(Message msg){
        // TODO -> Ajouter le message à la base de données et renvoyer le message aux clients concernés
        //this.dbm.addMessage(msg);
        Utilisateur sender = msg.getSender();
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

    //Fonctions interagissant avec la base de données
    public Utilisateur login(String username, String password) throws FalseLoginException {
        try{
            return this.dbm.loginAccount(username, password);
        }
        catch(FalseLoginException e){
            throw new FalseLoginException();
        }
        catch(Exception e){
            System.out.println("Erreur lors de la connexion au serveur.");
            return null;
        }
    }

    public Integer register(String username, String password) throws SQLException {
        try{
            return this.dbm.createAccount(username, password);
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            System.out.println("Erreur lors de la connexion au serveur.");
            return null;
        }
    }

    public Utilisateur loadUser(Integer idUser) throws SQLException {
        try{
            return this.dbm.loadUser(idUser);
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            System.out.println("Erreur lors de la connexion au serveur.");
            return null;
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        Server server = new Server();
        ServerSocket socketServeur = null;

        //Connection to DB
        try{
            server.connexionBD = new ConnexionBD();
            server.connexionBD.connecter();
            server.dbm = new DatabaseManager(server.connexionBD);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        //Server
        if(server.dbm != null){
            try {
            socketServeur = new ServerSocket(port);
    
                while (true) {
                    // Création d'un ClientHandler pour chaque nouvelle connexion
                    Socket socketClient = socketServeur.accept();
                    ClientHandler client = new ClientHandler(new Client(), socketClient, server);
                    server.clients.add(client);
                    client.start();
                }
            }
            catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
            finally {
                try {
                    if (socketServeur != null) socketServeur.close();
                }
                catch (IOException e) {
                    System.out.println("Error closing server socket: " + e.getMessage());
                }
            }
        }
    }
}