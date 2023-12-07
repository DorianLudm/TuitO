import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Message {
    private String date;
    private String message;
    private String pseudoAuteur;

    public Message(String message, String pseudoAuteur){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDate localDate = LocalDate.now();
        this.date = dtf.format(localDate);
        this.message = message;
        this.pseudoAuteur = pseudoAuteur;
    }
}
