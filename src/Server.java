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

    public void recu(String msg){ //TO SUPPR
        System.out.println(msg);
    }

    //Envoie du message à tout les utilisateurs connectés
    public void broadcast(String msg){
        for(ClientHandler liaisonClient : this.clients){
            liaisonClient.broadcast(msg);
        }
    }

    //Sauvegarde du message dans la base de données et envoie du message aux utilisateurs suivant l'utilisateur qui l'a envoyé
    public void broadcastFollower(Message msg){
        if(msg == null || msg.getMessage().trim().isEmpty()){
            return;
        }
        System.out.println(msg.toString());
        try{
            int idMsg = this.dbm.addMessage(msg);
            int idSender = msg.getSender().getId();
            List<Integer> followers = this.dbm.getFollowers(idSender);
            for(ClientHandler liaisonClient : this.clients){
                if(followers.contains(liaisonClient.getUser().getId())){
                    liaisonClient.broadcast(msg.toString(idMsg));
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("Erreur lors de la connexion au serveur.");
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

    public Utilisateur follow(Integer idUser, String idUserToFollow) throws SQLException, ServerIssueException{
        try{
            Utilisateur userFollowed = this.dbm.follow(idUser, idUserToFollow);
            return userFollowed;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    public Utilisateur unfollow(Integer idUser, String idUserToUnfollow) throws SQLException, ServerIssueException{
        try{
            Utilisateur userUnfollowed = this.dbm.unfollow(idUser, idUserToUnfollow);
            return userUnfollowed;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    public Integer like(Integer idUser, Integer idMessage) throws SQLException, ServerIssueException{
        try{
            Integer newNombreLikes = this.dbm.like(idUser, idMessage);
            return newNombreLikes;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    public Integer getNbLikes(Integer idMessage) throws SQLException, ServerIssueException{
        try{
            Integer nombreLikes = this.dbm.getNbLikes(idMessage);
            return nombreLikes;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
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
                    ClientHandler client = new ClientHandler(new Utilisateur(), socketClient, server);
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