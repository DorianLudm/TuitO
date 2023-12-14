import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{
    private Client client;
    private Socket socketClient;
    private String identiteServ;
    private int portServ;

    public ClientHandler(Client client, Socket socketClient, String identiteServ, int portServ){
        this.client = client;
        this.socketClient = socketClient;
        this.identiteServ = identiteServ;
        this.portServ = portServ;
    }

    public Client getClient(){
        return this.client;
    }

    // Envoie du message au client associé à l'instance de ClientHandler
    public void broadcast(String msg){
        try{
            PrintWriter writer = new PrintWriter(this.socketClient.getOutputStream(), true);
            writer.print(msg);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void run(){
        while(true){
            try{
                Socket socketClient = new Socket(identiteServ, portServ);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                String message = reader.readLine();
                System.out.println(message);
                socketClient.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
    }
}
