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

    public String getPseudo(){
        return this.pseudo;
    }

    public List<Message> getMessages(){
        return this.messages;
    }

    void main(){
        while(true){
            try{
                Socket socketClient = new Socket("localhost", 8080);
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