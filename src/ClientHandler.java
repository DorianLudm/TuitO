import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
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
                    this.broadcast("Erreur lors du suivi de l'utilisateur, veuillez réessayer.");
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
                    this.broadcast("Erreur lors du suivi de l'utilisateur, veuillez réessayer.");
                }
                break;
            case "/LIKE":
                try{
                    this.server.like(this.user.getId(), Integer.parseInt(command[1]));
                    this.broadcast("Tweet liké.");
                }
                catch(Exception e){
                    this.broadcast("Erreur lors du like du tweet, veuillez réessayer.");
                }
                break;
            // case "/DISLIKE":
            //     try{
            //         this.server.dislike(this.user.getId(), Integer.parseInt(command[1]));
            //         this.broadcast("Tweet disliké.");
            //     }
            //     catch(Exception e){
            //         this.broadcast("Erreur lors du dislike du tweet, veuillez réessayer.");
            //     }
            //     break;
            case "/GETLIKES":
            case "/GETNBLIKES":
            case "/NBLIKES":
                try{
                    Integer nbLikes = this.server.getNbLikes(Integer.parseInt(command[1]));
                    this.broadcast("Le tweet a " + nbLikes + " likes.");
                }
                catch(Exception e){
                    this.broadcast("Erreur lors de la récupération du nombre de likes du tweet, veuillez réessayer.");
                }
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
