import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

/**
 * La classe ClientHandler gère les interactions avec un client connecté au serveur de chat.
 */
public class ClientHandler extends Thread{
    private Utilisateur user;
    private Socket socketClient;
    private Server server;

    /**
     * Constructeur de la classe ClientHandler.
     * @param user L'utilisateur associé au client.
     * @param socketClient La socket du client connecté.
     * @param server Le serveur auquel le client est connecté.
     */
    public ClientHandler(Utilisateur user, Socket socketClient, Server server){
        this.user = user;
        this.socketClient = socketClient;
        this.server = server;
    }

    /**
     * Obtient l'utilisateur associé à ce gestionnaire de client.
     * @return L'utilisateur associé à ce gestionnaire de client.
     */
    public Utilisateur getUser(){
        return this.user;
    }

    /**
     * Envoie un message au client associé à l'instance de ClientHandler
     * @param message Le message à envoyer au client.
     */
    public void broadcast(String message) {
        try {
            PrintWriter writer = new PrintWriter(this.socketClient.getOutputStream(), true);
            writer.println(message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Ferme la connexion avec ce client et notifie le serveur de la fermeture.
     */
    public void close(){
        try {
            this.socketClient.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        this.server.close(this);
    }

    /**
     * Traite une commande reçue du client.
     * @param line La commande à traiter.
     */
    public void handleCommand(String line){
        this.server.recu(line); //TO SUPPR
        String[] command = line.split("&");
        switch(command[0].toUpperCase()){
            case "/LOGIN":
                try {
                    Utilisateur user = this.server.login(command[1], command[2]);
                    this.user = user;
                    this.broadcast("True&" + this.user.getPseudo() + "&" + this.user.getId());
                } catch(FalseLoginException e) {
                    this.broadcast("False");
                }
                catch(Exception e){
                    this.broadcast("ERROR");
                }
                break;
            case "/REGISTER":
                try{
                    if(command[1].contains(" ") || command[2].contains(" ")){
                        this.broadcast("Split");
                        break;
                    }
                    Integer idUser = this.server.register(command[1], command[2]);
                    Utilisateur user = this.server.loadUser(idUser);
                    this.user = user;
                    this.broadcast("True&" + this.user.getPseudo() + "&" + this.user.getId());
                }
                catch(SQLException e){
                    this.broadcast("Duplicate");
                }
                catch(Exception e){
                    this.broadcast("False");
                }
                break;
            case "/LOADMSG":
                try{
                    List<Message> messages = this.server.loadMsgUponLogin(Integer.parseInt(command[1]));
                    if(messages.size() > 10){
                        for(int i = messages.size()-10; i < messages.size(); i++){
                            Message message = messages.get(i);
                            this.broadcast(message.formatMessage());
                        }
                        this.broadcast("+ " + (messages.size() - 10) + " autres messages.");
                        this.broadcast("/newline");
                        break;
                    }
                    for (Message message : messages) {
                        this.broadcast(message.formatMessage());
                    }
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Erreur lors du chargement des messages, veuillez réessayer.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue lors du chargement des messages. \n Un problème interne est survenu.");
                }
                break;
            case "/FOLLOW":
                try{
                    Utilisateur utilisateurFollowed = this.server.follow(this.user.getId(), command[1]);
                    this.broadcast("Vous suivez désormais l'utilisateur '" + utilisateurFollowed.getPseudo() + "'.");
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Vous suivez déjà cet utilisateur, ou alors il n'existe pas.");
                }
                catch(Exception e){
                    this.broadcast("Erreur lors du suivi de l'utilisateur, veuillez réessayer. \n Veuillez vérifier que le paramètre id de l'utilisateur est bien un nombre.");
                }
                break;
            case "/UNFOLLOW":
                try{
                    Utilisateur utilisateurUnfollowed = this.server.unfollow(this.user.getId(), command[1]);
                    this.broadcast("Vous ne suivez désormais plus l'utilisateur '" + utilisateurUnfollowed.getPseudo() + "'.");
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Vous ne suivez déjà pas cet utilisateur, ou alors il n'existe pas.");
                }
                catch(Exception e){
                    this.broadcast("Erreur lors du suivi de l'utilisateur, veuillez réessayer. \n Veuillez vérifier que le paramètre id de l'utilisateur est bien un nombre.");
                }
                break;
            case "/LIKE":
                try{
                    int nbLikes = this.server.like(this.user.getId(), Integer.parseInt(command[1]));
                    this.broadcast("Tuit liké. Il a désormais " + nbLikes + " likes.");
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Vous aimez déjà ce tuit, ou alors il n'existe pas.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue avec le serveur. \n Veuillez vérifier que le paramètre id du tuit est bien un nombre.");
                }
                break;
            case "/UNLIKE":
                try{
                    int nbLikes = this.server.unlike(this.user.getId(), Integer.parseInt(command[1]));
                    this.broadcast("Vous n'aimez plus ce tuit. Il a désormais " + nbLikes + " likes.");
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Vous n'avez pas likez ce tuit, ou alors il n'existe pas.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue avec le serveur. \n Veuillez vérifier que le paramètre id du tuit est bien un nombre.");
                }
                break;
            case "/GETLIKES":
            case "/GETNBLIKES":
            case "/NBLIKES":
                try{
                    Integer nbLikes = this.server.getNbLikes(Integer.parseInt(command[1]));
                    this.broadcast("Ce tuit a " + nbLikes + " likes.");
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Erreur lors de la récupération du nombre de likes du tuit, veuillez réessayer.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue lors de la récupération du nombre de likes du tuit. \n Veuillez vérifier que le paramètre id du tuit est bien un nombre.");
                }
                break;
            case "/DELETE":
            case "/REMOVE":
                try{
                    this.server.deleteMsg(this.user.getId() ,Integer.parseInt(command[1]));
                    this.broadcast("Le tuit (" + command[1] + ") a été supprimé.");
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Vous n'êtes pas l'auteur de ce tuit, ou alors il n'existe pas.");
                }
                catch(Exception e){
                    this.broadcast("Erreur lors de la suppression du tuit. \n Veuillez vérifier que le paramètre id du tuit est bien un nombre.");
                }
            case "/HISTORY":
            case "/HISTORIQUE":
                try{
                    List<Message> historique = this.server.getHistorique(this.user.getId(), Integer.parseInt(command[1]));
                    if(historique.size() > 10){
                        for(int i = historique.size()-10; i < historique.size(); i++){
                            Message msg = historique.get(i);
                            this.broadcast(msg.formatMessage());
                        }
                        this.broadcast("+ " + (historique.size() - 10) + " autres messages.");
                    }
                    else{
                        if(historique.size() == 0){
                            this.broadcast("Vous n'avez pas encore envoyé de message :(");
                            break;
                        }
                        for (int i = historique.size() - 1; i >= 0; i--) {
                            Message msg = historique.get(i);
                            this.broadcast(msg.formatMessage());
                        }
                    }
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Erreur lors de la récupération de l'historique, veuillez réessayer.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue lors de la récupération de l'historique. \n Veuillez vérifier que le paramètre nombre de messages est bien un nombre.");
                }
                break;
            case "/FOLLOWERS":
                try{
                    List<Utilisateur> followers = this.server.getFollowers(this.user.getId());
                    if(followers.size() > 10){
                        for(int i = followers.size()-10; i < followers.size(); i++){
                            Utilisateur follower = followers.get(i);
                            this.broadcast(follower.getPseudo() + " - " + follower.getId());
                        }
                        this.broadcast("+ " + (followers.size() - 10) + " autres followers.");
                    }
                    else{
                        if(followers.size() == 0){
                            this.broadcast("Personne ne vous follow :(");
                            break;
                        }
                        for(Utilisateur follower : followers){
                            this.broadcast(follower.getPseudo() + " - " + follower.getId());
                        }
                    }
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Erreur lors de la récupération des followers, veuillez réessayer.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue lors de la récupération des followers.");
                }
                break;
            case "/FOLLOWING":
                try{
                    List<Utilisateur> followings = this.server.getFollowing(this.user.getId());
                    if(followings.size() > 10){
                        for(int i = followings.size()-10; i < followings.size(); i++){
                            Utilisateur following = followings.get(i);
                            this.broadcast(following.getPseudo() + " - " + following.getId());
                        }
                        this.broadcast("+ " + (followings.size() - 10) + " autres followings.");
                    }
                    else{
                        if(followings.size() == 0){
                            this.broadcast("Vous ne followez personne :(");
                            this.broadcast("/newline");
                            break;
                        }
                        for(Utilisateur following : followings){
                            this.broadcast(following.getPseudo() + " - " + following.getId());
                        }
                    }
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Erreur lors de la récupération de vos follows, veuillez réessayer.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue lors de la récupération de vos follows.");
                }
                break;
            case "/GETMSG":
                try{
                    List<Message> messages = this.server.getMsg(command[1]);
                    if(messages.size() > 10){
                        this.broadcast("Voici les 10 derniers messages de l'utilisateur " + command[1] + ":");
                        for(int i = messages.size()-10; i < messages.size(); i++){
                            Message message = messages.get(i);
                            this.broadcast(message.formatMessage());
                        }
                        this.broadcast("+ " + (messages.size() - 10) + " autres messages.");
                        this.broadcast("/newline");
                        break;
                    }
                    else{
                        if(messages.size() == 0){
                            this.broadcast("Cet utilisateur n'a pas encore posté de message.");
                            this.broadcast("/newline");
                            break;
                        }
                        this.broadcast("Voici les derniers messages de l'utilisateur " + command[1] + ":");
                        for(Message message : messages){
                            this.broadcast(message.formatMessage());
                        }
                    }
                    this.broadcast("/newline");
                }
                catch(SQLException e){
                    this.broadcast("Erreur lors de la récupération des messages de l'utilisateur '"+ command[1] + "', veuillez vérifier qu'il existe.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue lors de la récupération des messages de l'utilisateur. \n Veuillez vérifier que le paramètre de l'utilisateur est bien donné.");
                }
                break;
            case "/QUIT":
                this.broadcast("/QUIT");
                this.server.close(this);
                this.close();
                break;
        }
    }

    /**
     * Traite un message JSON reçu du client.
     * @param line Le message JSON à traiter.
     */
    public void handleMessage(String line){
        Gson gson = new Gson();
        Message message = gson.fromJson(line, Message.class);
        this.server.broadcastFollower(message);
    }

    /**
     * Exécute le thread, gérant les entrées du client.
     */
    @Override
    public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                if(line.startsWith("/")){
                    this.handleCommand(line);
                }
                else{
                    this.handleMessage(line);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            this.close();
        }
    }
}
