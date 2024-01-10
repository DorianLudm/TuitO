import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Message {
    private String dateEnvoi;
    private String message;
    private Client sender;
    private int nbLikes = 0;

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

    public Client getSender(){
        return this.sender;
    }

    public String getPseudoClient(){
        return this.sender.getUser().getPseudo();
    }

    public void incrLikes(){
        this.nbLikes += 1;
    }

    public void decrLikes(){
        this.nbLikes -= 1;
    }

    @Override
    public String toString() {
        return this.getPseudoClient() + ": " + this.getMessage();
    }

    public void uploadBD(){
        // TODO -> Upload le message dans la BD et renvoie une exception si ça a échoué
    }
}
