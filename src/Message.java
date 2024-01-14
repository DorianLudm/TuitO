import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private String dateEnvoi;
    private String message;
    private Utilisateur sender;
    private List<Utilisateur> likes;
    private int nbLikes;

    public Message(String message, Utilisateur autheur){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDate localDate = LocalDate.now();
        this.dateEnvoi = dtf.format(localDate);
        this.message = message;
        this.sender = autheur;
        this.likes = new ArrayList<Utilisateur>();
        this.nbLikes = 0;
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

    public String getPseudoClient(){
        return this.sender.getPseudo();
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
}
