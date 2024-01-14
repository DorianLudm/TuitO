import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{
    private Client client;
    private Socket socketClient;
    private Server server;

    public ClientHandler(Client client, Socket socketClient, Server server){
        this.client = client;
        this.socketClient = socketClient;
        this.server = server;
    }

    public Client getClient(){
        return this.client;
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
        String[] command = line.split("&");
        switch(command[0]){
            case "/LOGIN":
                try {
                    Utilisateur user = this.server.login(command[1], command[2]);
                    this.client.setUser(user);
                    this.broadcast("True&" + user.getPseudo());
                } catch (FalseLoginException e) {
                    this.broadcast("False");
                }
                break;
            // case "/REGISTER":
            //     try{
            //         this.client.register(command[1], command[2]);
            //         this.broadcast("Vous êtes connecté en tant que " + this.client.getUser().getUsername());
            //     }
            //     catch(Exception e){
            //         this.broadcast("Erreur lors de l'inscription, veuillez réessayer.");
            //     }
            //     break;
            // case "/FOLLOW":
            //     try{
            //         this.client.follow(command[1]);
            //         this.broadcast("Vous suivez désormais " + command[1]);
            //     }
            //     catch(Exception e){
            //         this.broadcast("Erreur lors du suivi de l'utilisateur, veuillez réessayer.");
            //     }
            //     break;
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
                    Message message = new Message(line, this.client.getUser());
                    this.server.broadcastFollower(message);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            this.close();
        }
    }
}
