import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client{
    private String pseudo;
    private List<Message> messages;

    public Client(String pseudo){
        this.pseudo = pseudo;
        this.messages = new ArrayList<Message>();
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