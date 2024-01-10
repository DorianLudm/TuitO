import java.util.ArrayList;
import java.util.List;

public class Utilisateur {
    private String pseudo;
    List<Message> messages;
    List<Utilisateur> followers;
    List<Utilisateur> following;

    public Utilisateur(String pseudo){
        this.pseudo = pseudo;
        this.messages = new ArrayList<Message>();
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
}
