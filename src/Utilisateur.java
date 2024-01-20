import java.util.ArrayList;
import java.util.List;

/**
 * Représente un utilisateur du système.
 */
public class Utilisateur {
    private int id;
    private String pseudo;

    /**
     * Constructeur par défaut.
     */
    public Utilisateur(){
    }
    
    /**
     * Constructeur avec spécification de l'identifiant et du pseudo.
     *
     * @param id L'identifiant unique de l'utilisateur.
     * @param pseudo Le pseudo de l'utilisateur.
     */
    public Utilisateur(int id, String pseudo){
        this.id = id;
        this.pseudo = pseudo;
    }

    /**
     * Constructeur avec spécification de l'identifiant, du pseudo, des messages et des utilisateurs suivis.
     *
     * @param id L'identifiant unique de l'utilisateur.
     * @param pseudo Le pseudo de l'utilisateur.
     * @param messages La liste des messages de l'utilisateur.
     * @param following La liste des utilisateurs suivis par l'utilisateur.
     */
    public Utilisateur(int id, String pseudo, List<Message> messages, List<Utilisateur> following){
        this.id = id;
        this.pseudo = pseudo;
    }

    /**
     * Définit l'identifiant de l'utilisateur.
     *
     * @param id L'identifiant unique de l'utilisateur.
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Obtient l'identifiant de l'utilisateur.
     *
     * @return L'identifiant unique de l'utilisateur.
     */
    public int getId(){
        return this.id;
    }

    /**
     * Définit le pseudo de l'utilisateur.
     *
     * @param pseudo Le pseudo de l'utilisateur.
     */
    public void setPseudo(String pseudo){
        this.pseudo = pseudo;
    }
    
    /**
     * Obtient le pseudo de l'utilisateur.
     *
     * @return Le pseudo de l'utilisateur.
     */
    public String getPseudo(){
        return this.pseudo;
    }
    
    /**
     * Override de la méthode toString pour obtenir une représentation textuelle de l'utilisateur.
     *
     * @return Le pseudo de l'utilisateur.
     */
    @Override
    public String toString(){
        return this.pseudo;
    }
}
