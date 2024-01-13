import java.util.ArrayList;
import java.util.List;

public class Utilisateur {
    private int id;
    private String pseudo;
    List<Message> messages;
    List<Utilisateur> followers;
    List<Utilisateur> following;

    public Utilisateur(){
        this.messages = new ArrayList<Message>();
        this.followers = new ArrayList<Utilisateur>();
        this.following = new ArrayList<Utilisateur>();
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

    public List<Message> getMessages(){
        return this.messages;
    }

    public List<Utilisateur> getFollowers(){
        return this.followers;
    }

    public List<Utilisateur> getFollowing(){
        return this.following;
    }

    public void addMessage(Message message){
        this.messages.add(message);
    }

    public void addFollower(Utilisateur follower){
        this.followers.add(follower);
    }

    public void addFollowing(Utilisateur following){
        this.following.add(following);
    }

    @Override
    public String toString(){
        return this.pseudo;
    }
}
