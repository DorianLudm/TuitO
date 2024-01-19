import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;

public class Message {
    private String dateEnvoi;
    private String message;
    private Utilisateur sender;
    private Integer idMessage;

    public Message(String message, Utilisateur sender){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        this.dateEnvoi = dtf.format(localDateTime);
        this.message = message;
        this.sender = sender;
    }

    public Message(String message, Utilisateur sender, Integer idMessage){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        this.dateEnvoi = dtf.format(localDateTime);
        this.message = message;
        this.sender = sender;
        this.idMessage = idMessage;
    }

    public Message(String dateEnvoie, String message, Utilisateur sender, Integer idMessage){
        this.dateEnvoi = dateEnvoie;
        this.message = message;
        this.sender = sender;
        this.idMessage = idMessage;
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

    public Integer getIdMessage(){
        return this.idMessage;
    }

    public String formatMessage(){
        String[] dateTime = this.dateEnvoi.split(" ");
        String date = dateTime[0];
        String time = dateTime[1];
        return "ID Message: " + (this.idMessage != null ? this.idMessage : "N/A") + " - " + this.sender.getPseudo() + " a Tuité à " + time + " le " + date + " - " + this.message;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return "{ \"dateEnvoi\": \"" + this.dateEnvoi + "\", \"message\": \"" + this.message + "\", \"sender\": " + gson.toJson(this.sender) + " }";
    }

    public String toString(int idMessage) {
        Gson gson = new Gson();
        return "{ \"idMessage\": " + idMessage + ", \"dateEnvoi\": \"" + this.dateEnvoi + "\", \"message\": \"" + this.message + "\", \"sender\": " + gson.toJson(this.sender) + " }";
    }
}
