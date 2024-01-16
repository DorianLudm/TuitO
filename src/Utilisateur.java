import java.util.ArrayList;
import java.util.List;

public class Utilisateur {
    private int id;
    private String pseudo;

    public Utilisateur(){
    }
    
    public Utilisateur(int id, String pseudo){
        this.id = id;
        this.pseudo = pseudo;
    }

    public Utilisateur(int id, String pseudo, List<Message> messages, List<Utilisateur> following){
        this.id = id;
        this.pseudo = pseudo;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setPseudo(String pseudo){
        this.pseudo = pseudo;
    }
    
    public String getPseudo(){
        return this.pseudo;
    }
    @Override
    public String toString(){
        return this.pseudo;
    }
}
