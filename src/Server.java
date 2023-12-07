import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class Server{
    private List<Thread> clients;

    public Server(){
        clients = new ArrayList<Thread>();
    }

    public void start() throws IOException{
        int port = 5555;
        while(true){
            ServerSocket socketServeur = new ServerSocket(port);
            Socket socketClient = socketServeur.accept();
            Socket socketToClient = new Socket(socketClient.getInetAddress(), 5555);
            Thread thread = // Client Handler (non implémenté)
            thread.start();
            clients.add(thread);
            PrintWriter writer = new PrintWriter(socketToClient.getOutputStream(), true);
            writer.print("Bienvenue sur le serveur");
            socketServeur.close();
        }
        
    }
}