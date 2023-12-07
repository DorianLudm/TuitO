import java.io.*;
import java.net.*;

public class Client{
    private String pseudo;

    public Client(String pseudo){
        this.pseudo = pseudo;
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