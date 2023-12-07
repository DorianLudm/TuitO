import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler extends Thread{
    private Client client;
    private String identiteServ;
    private int portServ;

    public ClientHandler(Client client, String identiteServ, int portServ){
        this.client = client;
        this.identiteServ = identiteServ;
        this.portServ = portServ;
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
