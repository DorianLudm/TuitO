import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class Server{
    private List<ClientHandler> clients;
    private String identiteServ;
    private int portServ;

    public Server(){
        clients = new ArrayList<ClientHandler>();
        identiteServ = "localhost";
        portServ = 8080;
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

    void main() throws IOException{
        int port = 5555;
        while(true){
            ServerSocket socketServeur = new ServerSocket(port);
            // Lors d'une connexion, on crée un nouveau thread pour le client
            Socket socketClient = socketServeur.accept();
            Socket socketToClient = new Socket(socketClient.getInetAddress(), 5555);
            String addrClient = socketClient.getRemoteSocketAddress().toString();
            ClientHandler client = new ClientHandler(new Client(addrClient), socketClient, this.identiteServ, this.portServ);
            clients.add(client);
            client.start();
            // On envoie un message à l'ensemble des clients
            PrintWriter writer = new PrintWriter(socketToClient.getOutputStream(), true);
            writer.print("Un nouveau client vient de se connecter! Il s'agit de" + addrClient);
            socketServeur.close();
        }
    }
}