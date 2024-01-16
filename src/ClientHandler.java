import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

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
                    Utilisateur utilisateurFollowed = this.server.follow(this.user.getId(), Integer.parseInt(command[1]));
                    this.broadcast("Vous suivez désormais " + utilisateurFollowed.getPseudo() + "(" + command[1] + ")" + ".");
                }
                catch(SQLException e){
                    this.broadcast("Vous suivez déjà cet utilisateur, ou alors il n'existe pas.");
                }
                catch(Exception e){
                    this.broadcast("Erreur lors du suivi de l'utilisateur, veuillez réessayer.");
                }
                break;
            // case "/UNFOLLOW":
            //     try{
            //         this.client.unfollow(command[1]);
            //         this.broadcast("Vous ne suivez plus " + command[1]);
            //     }
            //     catch(Exception e){
            //         this.broadcast("Erreur lors de l'arrêt du suivi de l'utilisateur, veuillez réessayer.");
            //     }
            //     break;
            // case "/TWEET":
            //     try{
            //         this.client.tweet(command[1]);
            //         this.broadcast("Tweet envoyé.");
            //     }
            //     catch(Exception e){
            //         this.broadcast("Erreur lors de l'envoi du tweet, veuillez réessayer.");
            //     }
            //     break;
            // case "/LIKE":
            //     try{
            //         this.client.like(Integer.parseInt(command[1]));
            //         this.broadcast("Tweet liké.");
            //     }
            //     catch(Exception e){
            //         this.broadcast("Erreur lors du like du tweet, veuillez réessayer.");
            //     }
            //     break;
            // case "/RETWEET":
            //     try{
            //         this.client.retweet(Integer.parseInt(command[1]));
            //         this.broadcast("Tweet retweeté.");
            //     }
            //     catch(Exception e){
            //         this.broadcast("Erreur lors du retweet du tweet, veuillez réessayer.");
            //     }
            //     break;
            // case "/COMMENT":
            //     try{
            //         this.client.comment(Integer.parseInt(command[1]), command[2]);
            //         this.broadcast("Commentaire envoyé");
        }
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
                    Message message = new Message(line, this.user);
                    this.server.broadcastFollower(message);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            this.close();
        }
    }
}
