import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

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
    public void broadcast(String msg, Client client){
        for(ClientHandler liaisonClient : this.clients){
            if(liaisonClient.getClient() != client){
                liaisonClient.broadcast(msg);
            }
        }
    }

    //Fermeture de la connexion avec le client
    public void close(ClientHandler client){
        this.clients.remove(client);
    }

    public static void main(String[] args) throws IOException{
        int port = 8080;
        Server server = new Server();
        while(true){
            ServerSocket socketServeur = new ServerSocket(port);
            // Lors d'une connexion, on crée un nouveau thread pour le client
            Socket socketClient = socketServeur.accept();
            String addrClient = socketClient.getRemoteSocketAddress().toString();
            ClientHandler client = new ClientHandler(new Client(addrClient), socketClient, server);
            server.clients.add(client);
            client.start();
            // On envoie un message à l'ensemble des clients
            server.broadcast("Nouvelle connexion de " + addrClient);
        }
    }
}