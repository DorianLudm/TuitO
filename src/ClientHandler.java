import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

public class ClientHandler extends Thread{
    private Utilisateur user;
    private Socket socketClient;
    private Server server;

    public ClientHandler(Utilisateur user, Socket socketClient, Server server){
        this.user = user;
        this.socketClient = socketClient;
        this.server = server;
    }

    public Utilisateur getUser(){
        return this.user;
    }

    // Envoie du message au client associé à l'instance de ClientHandler
    public void broadcast(String message) {
        try {
            PrintWriter writer = new PrintWriter(this.socketClient.getOutputStream(), true);
            writer.println(message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void close(){
        try {
            this.socketClient.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        this.server.close(this);
    }

    public void handleCommand(String line){
        this.server.recu(line); //TO SUPPR
        String[] command = line.split("&");
        switch(command[0].toUpperCase()){
            case "/LOGIN":
                try {
                    Utilisateur user = this.server.login(command[1], command[2]);
                    this.user = user;
                    this.broadcast("True&" + this.user.getPseudo() + "&" + this.user.getId());
                } catch (FalseLoginException e) {
                    this.broadcast("False");
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
            case "/FOLLOW":
                try{
                    Utilisateur utilisateurFollowed = this.server.follow(this.user.getId(), command[1]);
                    this.broadcast("Vous suivez désormais l'utilisateur '" + utilisateurFollowed.getPseudo() + "'.");
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
                    for (int i = historique.size(); i >=  historique.size() - 10; i--) {
                        Message message = historique.get(i);
                        this.broadcast(message.formatMessage());
                    }
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
                            break;
                        }
                        for(Utilisateur following : followings){
                            this.broadcast(following.getPseudo() + " - " + following.getId());
                        }
                    }
                }
                catch(SQLException e){
                    this.broadcast("Erreur lors de la récupération de vos follows, veuillez réessayer.");
                }
                catch(Exception e){
                    this.broadcast("Une erreur est survenue lors de la récupération de vos follows.");
                }
                break;
            case "/QUIT":
                this.broadcast("/QUIT");
                this.server.close(this);
                this.close();
                break;
        }
    }

    public void handleMessage(String line){
        Gson gson = new Gson();
        Message message = gson.fromJson(line, Message.class);
        this.server.broadcastFollower(message);
    }

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
