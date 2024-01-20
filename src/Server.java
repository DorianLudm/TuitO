import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;

/**
 * Classe représentant le serveur central du système de messagerie.
 */
public class Server{
    private List<ClientHandler> clients;
    private DatabaseManager dbm;
    private ConnexionBD connexionBD;

    /**
     * Constructeur par défaut de la classe Server.
     */
    public Server(){
        clients = new ArrayList<ClientHandler>();
        this.connexionBD = null;
        this.dbm = null;
    }

    /**
     * Méthode qui affiche un message reçu (à supprimer dans la version finale).
     * @param msg Le message reçu.
     */
    public void recu(String msg){ //TO SUPPR
        System.out.println(msg);
    }

    /**
     * Envoie d'un message à tous les utilisateurs connectés.
     * @param msg Le message à diffuser.
     */
    public void broadcast(String msg){
        for(ClientHandler liaisonClient : this.clients){
            liaisonClient.broadcast(msg);
        }
    }

    /**
     * Envoie d'un message à tous les followers de l'utilisateur qui l'a envoyé,
     * enregistrement du message dans la base de données.
     * @param msg Le message à diffuser.
     */
    public void broadcastFollower(Message msg){
        if(msg == null || msg.getMessage().trim().isEmpty()){
            return;
        }
        System.out.println(msg.toString());
        try{
            int idMsg = this.dbm.addMessage(msg);
            int idSender = msg.getSender().getId();
            List<Integer> followers = this.dbm.getIdFollowers(idSender);
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

    /**
    * Fermeture de la connexion avec un client.
    * @param client Le gestionnaire de client à déconnecter.
    */
    public void close(ClientHandler client){
        this.clients.remove(client);
    }

    //Fonctions interagissant avec la base de données

    /**
     * Connexion d'un utilisateur avec un nom d'utilisateur et un mot de passe.
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     * @return L'utilisateur connecté.
     * @throws FalseLoginException En cas d'échec de connexion.
     */
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

    /**
     * Enregistrement d'un nouvel utilisateur avec un nom d'utilisateur et un mot de passe.
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     * @return L'identifiant de l'utilisateur enregistré.
     * @throws SQLException En cas d'échec d'enregistrement.
     */
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

    /**
     * Chargement des informations d'un utilisateur à partir de son identifiant.
     * @param idUser L'identifiant de l'utilisateur.
     * @return L'utilisateur chargé.
     * @throws SQLException En cas d'échec de chargement.
     */
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

    /**
     * Permet à un utilisateur de suivre un autre utilisateur.
     * 
     * @param idUser L'identifiant de l'utilisateur qui souhaite suivre.
     * @param idUserToFollow L'identifiant de l'utilisateur à suivre.
     * @return L'utilisateur suivi.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
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

    /**
     * Permet à un utilisateur de ne plus suivre un autre utilisateur.
     * 
     * @param idUser L'identifiant de l'utilisateur qui souhaite arrêter de suivre.
     * @param idUserToUnfollow L'identifiant de l'utilisateur à ne plus suivre.
     * @return L'utilisateur qui n'est plus suivi.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
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

    /**
     * Permet à un utilisateur de donner un "like" à un message.
     * 
     * @param idUser L'identifiant de l'utilisateur qui donne le "like".
     * @param idMessage L'identifiant du message à "liker".
     * @return Le nouveau nombre de "likes" pour le message.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
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

    /**
     * Permet à un utilisateur d'annuler son "like" sur un message.
     * 
     * @param idUser L'identifiant de l'utilisateur qui annule le "like".
     * @param idMessage L'identifiant du message sur lequel le "like" est annulé.
     * @return Le nouveau nombre de "likes" pour le message.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
    public Integer unlike(Integer idUser, Integer idMessage) throws SQLException, ServerIssueException{
        try{
            Integer newNombreLikes = this.dbm.unlike(idUser, idMessage);
            return newNombreLikes;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    /**
     * Récupère le nombre total de "likes" pour un message donné.
     * 
     * @param idMessage L'identifiant du message.
     * @return Le nombre de "likes" pour le message.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
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

    /**
     * Supprime un message spécifique d'un utilisateur.
     * 
     * @param idUser L'identifiant de l'utilisateur propriétaire du message.
     * @param idMessage L'identifiant du message à supprimer.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
    public void deleteMsg(Integer idUser, Integer idMessage) throws SQLException, ServerIssueException{
        try{
            this.dbm.deleteMsg(idUser, idMessage);
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    /**
     * Supprime tous les messages, utilisateurs, "likes" et relations de la base de données.
     * 
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
    public void deleteAll() throws SQLException, ServerIssueException{
        try{
            this.dbm.deleteAll();
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    /**
     * Récupère l'historique des messages d'un utilisateur.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur.
     * @param nombreMessages Le nombre de messages à récupérer dans l'historique.
     * @return La liste des messages dans l'historique.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
    public List<Message> getHistorique(Integer idUtilisateur, int nombreMessages) throws SQLException, ServerIssueException{
        try{
            List<Message> historique = this.dbm.getHistorique(idUtilisateur, nombreMessages);
            return historique;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    /**
     * Récupère la liste des utilisateurs qui suivent un utilisateur donné.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur suivi.
     * @return La liste des utilisateurs qui suivent l'utilisateur donné.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
    public List<Utilisateur> getFollowers(Integer idUtilisateur) throws SQLException, ServerIssueException{
        try{
            List<Utilisateur> followers = this.dbm.getFollowers(idUtilisateur);
            return followers;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    /**
     * Récupère la liste des utilisateurs suivis par un utilisateur donné.
     * 
     * @param idUtilisateur L'identifiant de l'utilisateur qui suit.
     * @return La liste des utilisateurs suivis par l'utilisateur donné.
     * @throws SQLException En cas d'erreur lors de l'accès à la base de données.
     * @throws ServerIssueException En cas d'incident côté serveur.
     */
    public List<Utilisateur> getFollowing(Integer idUtilisateur) throws SQLException, ServerIssueException{
        try{
            List<Utilisateur> followers = this.dbm.getFollowing(idUtilisateur);
            return followers;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    public List<Message> loadMsgUponLogin(Integer idUtilisateur) throws SQLException, ServerIssueException{
        try{
            List<Message> messages = this.dbm.loadMsgUponLogin(idUtilisateur);
            return messages;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    public List<Message> getMsg(String pseudoUtilisateur) throws SQLException, ServerIssueException{
        try{
            List<Message> messages = this.dbm.getMsg(pseudoUtilisateur);
            return messages;
        }
        catch(SQLException e){
            throw new SQLException();
        }
        catch(Exception e){
            throw new ServerIssueException();
        }
    }

    public static void main(String[] args) {
      /**
     * Méthode principale pour exécuter le serveur.
     * @param args Les arguments de la ligne de commande.
     */
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

        final DatabaseManager dbm = server.dbm;
        //Terminal Server
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("");
                    String input = scanner.nextLine();
                    String[] command = input.split(" ");
                    switch (command[0].toUpperCase()) {
                        case "/DELETEMSG":
                            try{
                                dbm.deleteMsg(Integer.parseInt(command[1]));
                                System.out.println("Message (" + command[1] + ") supprimé.");
                            }
                            catch(Exception e){
                                System.out.println("Erreur lors de la suppression du message, veuillez vérifier qu'il existe bien. \n");
                            }
                            break;
                        case "/DELETEUSER":
                            try{
                                String username = dbm.deleteUser(command[1]);
                                System.out.println("L'utilisateur '" + username + "' a été supprimé.");
                            }
                            catch(Exception e){
                                System.out.println("Erreur lors de la suppression de l'utilisateur, veuillez vérifier qu'il existe bien. \n");
                            }
                            break;
                        case "/DELETEALL":
                            try{
                                dbm.deleteAll();
                                System.out.println("Tous les utilisateurs et messages ont été supprimés.");
                            }
                            catch(Exception e){
                                System.out.println("Erreur lors de la suppression des utilisateurs et messages. \n");
                            }
                            break;
                    }
                }
            }
        }).start();

        //Server
        if(server.dbm != null){
            if(server.connexionBD.isConnecte()){
                System.out.println("Connexion à la base de données réussie.");
            }
            else{
                System.out.println("Erreur lors de la connexion à la base de données.");
                System.exit(0);
            }
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