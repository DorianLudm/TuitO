import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;

public class Message {
    private String dateEnvoi;
    private String message;
    private Utilisateur sender;

    public Message(String message, Utilisateur sender){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        this.dateEnvoi = dtf.format(localDateTime);
        this.message = message;
        this.sender = sender;
    }

    public String getDate(){
        return this.dateEnvoi;
    }

    public String getMessage(){
        return this.message;
    }

    public Utilisateur getSender(){
        return this.sender;
    }

    public String formatMessage(){
        return this.sender.getPseudo() + " (" + this.dateEnvoi + ") : " + this.message;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return "{ \"dateEnvoi\": \"" + this.dateEnvoi + "\", \"message\": \"" + this.message + "\", \"sender\": " + gson.toJson(this.sender) + " }";
    }
}
