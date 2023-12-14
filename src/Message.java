import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Message {
    private String dateEnvoi;
    private String message;
    private Client sender;

    public Message(String message, Client autheur){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDate localDate = LocalDate.now();
        this.dateEnvoi = dtf.format(localDate);
        this.message = message;
        this.sender = autheur;
    }

    public String getDate(){
        return this.dateEnvoi;
    }

    public String getMessage(){
        return this.message;
    }

    public Client getAutheur(){
        return this.sender;
    }
}
