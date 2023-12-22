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

    @Override
    public void run(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                this.server.broadcast(line, this.client);
            }
        } catch (Exception e) {
            System.out.println(e);
            this.close();
        }
    }
}
