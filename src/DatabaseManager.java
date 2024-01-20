import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * La classe DatabaseManager gère les opérations sur la base de données liées aux utilisateurs et aux messages.
 */
public class DatabaseManager {
    private ConnexionBD connexionBD;
    private Statement st;

    /**
     * Constructeur de la classe DatabaseManager.
     * @param connexion La connexion à la base de données.
     */
    public DatabaseManager(ConnexionBD connexion){
        this.connexionBD = connexion;
    }

    /**
     * Crée un nouveau compte utilisateur dans la base de données.
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     * @return L'identifiant du nouvel utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Integer createAccount(String username, String password) throws SQLException{
        String hashedPassword = hash(password);
        int newId = newIdUser();
        this.st = this.connexionBD.createStatement();
        PreparedStatement s = this.connexionBD.prepareStatement("insert into UTILISATEUR values (?, ? ,?)");
        s.setInt(1, newId);
        s.setString(2, username);
        s.setString(3, hashedPassword);
        s.executeUpdate();
        return newId;
    }

    /**
     * Génère un nouvel identifiant d'utilisateur.
     * @return Le nouvel identifiant d'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public int newIdUser() throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select max(idUtilisateur) from UTILISATEUR;");
        if (rs.next()){
            int maxint = rs.getInt(1);
            return (maxint+1);
        }
        return 1;
    }

    /**
     * Connecte un utilisateur à la base de données.
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     * @return L'utilisateur connecté.
     * @throws SQLException En cas d'erreur SQL.
     * @throws FalseLoginException Si la connexion échoue.
     */
    public Utilisateur loginAccount(String username, String password) throws SQLException, FalseLoginException{
        this.st = this.connexionBD.createStatement();
        String hashedPassword = hash(password);
        ResultSet rs = this.st.executeQuery("select * from UTILISATEUR where pseudo='"+ username +"' and password ='" + hashedPassword +"'");
        if(rs.next()){
            int id = rs.getInt(1);
            Utilisateur userLogined = new Utilisateur();
            userLogined.setId(id);
            userLogined.setPseudo(username);
            return userLogined;
        }
        else{
            throw new FalseLoginException();
        }
    }

    /**
     * Charge les informations d'un utilisateur à partir de l'identifiant.
     * @param idUser L'identifiant de l'utilisateur.
     * @return L'utilisateur chargé.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Utilisateur loadUser(Integer idUser) throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select * from UTILISATEUR where idUtilisateur='"+ idUser +"'");
        if(rs.next()){
            String pseudo = rs.getString(2);
            Utilisateur user = new Utilisateur();
            user.setId(idUser);
            user.setPseudo(pseudo);
            return user;
        }
        else{
            throw new SQLException();
        }
    }
    
    /**
     * Permet à un utilisateur de suivre un autre utilisateur.
     * @param idUser L'identifiant de l'utilisateur.
     * @param pseudoUserToFollow Le pseudo de l'utilisateur à suivre.
     * @return L'utilisateur suivi.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Utilisateur follow(Integer idUser, String pseudoUserToFollow) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            int idUserToFollow = getId(pseudoUserToFollow);
            PreparedStatement s = this.connexionBD.prepareStatement("insert into FOLLOW values (?, ?)");
            s.setInt(1, idUser);
            s.setInt(2, idUserToFollow);
            s.executeUpdate();
            Utilisateur user = new Utilisateur(idUserToFollow, pseudoUserToFollow);
            return user;
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Arrête de suivre un utilisateur.
     * @param idUser L'identifiant de l'utilisateur.
     * @param pseudoUserToUnfollow Le pseudo de l'utilisateur à ne plus suivre.
     * @return L'utilisateur qui n'est plus suivi.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Utilisateur unfollow(Integer idUser, String pseudoUserToUnfollow) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            int idUserToUnfollow = getId(pseudoUserToUnfollow);
            PreparedStatement s = this.connexionBD.prepareStatement("delete from FOLLOW where idUtilisateur1=? and idUtilisateur2=?");
            s.setInt(1, idUser);
            s.setInt(2, idUserToUnfollow);
            int nbLigneSuppr = s.executeUpdate();
            if(nbLigneSuppr == 0){
                throw new SQLException();
            }
            Utilisateur user = new Utilisateur(idUserToUnfollow, pseudoUserToUnfollow);
            return user;
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Récupère le pseudo d'un utilisateur à partir de son identifiant.
     * @param idUser L'identifiant de l'utilisateur.
     * @return Le pseudo de l'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public String getPseudo(int idUser) throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select pseudo from UTILISATEUR where idUtilisateur='"+ idUser +"'");
        if(rs.next()){
            String pseudo = rs.getString(1);
            return pseudo;
        }
        else{
            throw new SQLException();
        }
    }

    /**
     * Récupère l'identifiant d'un utilisateur à partir de son pseudo.
     * @param pseudo Le pseudo de l'utilisateur.
     * @return L'identifiant de l'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public int getId(String pseudo) throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select idUtilisateur from UTILISATEUR where pseudo='"+ pseudo +"'");
        if(rs.next()){
            int id = rs.getInt(1);
            return id;
        }
        else{
            throw new SQLException();
        }
    }

    /**
     * Ajoute un message à la base de données.
     * @param message Le message à ajouter.
     * @return L'identifiant du nouveau message.
     * @throws SQLException En cas d'erreur SQL.
     */
    public int addMessage(Message message) throws SQLException{
        this.st = this.connexionBD.createStatement();
        int newIdMessage = newIdMessage();
        PreparedStatement s = this.connexionBD.prepareStatement("insert into MESSAGES values (?, ?, ?, ?)");
        s.setInt(1, newIdMessage);
        s.setInt(2, message.getSender().getId());
        s.setString(3, message.getMessage());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(message.getDate(), formatter);
        s.setTimestamp(4, java.sql.Timestamp.valueOf(dateTime));
        s.executeUpdate();
        return newIdMessage;
    }

    /**
     * Génère un nouvel identifiant de message.
     * @return Le nouvel identifiant de message.
     * @throws SQLException En cas d'erreur SQL.
     */
    public int newIdMessage() throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select max(idMessage) from MESSAGES;");
        if (rs.next()){
            int maxint = rs.getInt(1);
            return (maxint+1);
        }
        return 1;
    }

    /**
     * Récupère les identifiants des followers d'un utilisateur.
     * @param idSender L'identifiant de l'utilisateur suivi.
     * @return Une liste des identifiants des followers.
     * @throws SQLException En cas d'erreur SQL.
     */
    public List<Integer> getIdFollowers(int idSender) throws SQLException{
        try{
            this.st = this.connexionBD.createStatement();
            ResultSet rs = this.st.executeQuery("select idUtilisateur1 from FOLLOW where idUtilisateur2='"+ idSender +"'");
            List<Integer> followers = new ArrayList<>();
            while(rs.next()){
                int idFollower = rs.getInt(1);
                followers.add(idFollower);
            }
            return followers;
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Donne un like à un message.
     * @param idUser L'identifiant de l'utilisateur qui donne le like.
     * @param idMessage L'identifiant du message aimé.
     * @return Le nombre total de likes du message.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Integer like(Integer idUser, Integer idMessage) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            PreparedStatement s = this.connexionBD.prepareStatement("insert into LIKES values (?, ?)");
            s.setInt(1, idUser);
            s.setInt(2, idMessage);
            s.executeUpdate();
            return getNbLikes(idMessage);
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Annule un like donné à un message.
     * @param idUser L'identifiant de l'utilisateur qui annule le like.
     * @param idMessage L'identifiant du message dont le like est annulé.
     * @return Le nombre total de likes du message après annulation.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Integer unlike(Integer idUser, Integer idMessage) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            PreparedStatement s = this.connexionBD.prepareStatement("delete from LIKES where idUtilisateur=? and idMessage=?");
            s.setInt(1, idUser);
            s.setInt(2, idMessage);
            int nbLigneSuppr = s.executeUpdate();
            if(nbLigneSuppr == 0){
                throw new SQLException();
            }
            return getNbLikes(idMessage);
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Récupère le nombre total de likes d'un message.
     * @param idMessage L'identifiant du message.
     * @return Le nombre total de likes du message.
     * @throws SQLException En cas d'erreur SQL.
     */
    public Integer getNbLikes(Integer idMessage) throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select count(*) from LIKES where idMessage='"+ idMessage +"'");
        if(rs.next()){
            int nbLikes = rs.getInt(1);
            return nbLikes;
        }
        else{
            throw new SQLException();
        }
    }

    /**
     * Supprime un message de la base de données.
     * @param idUser L'identifiant de l'utilisateur qui supprime le message.
     * @param idMessage L'identifiant du message à supprimer.
     * @throws SQLException En cas d'erreur SQL.
     */
    public void deleteMsg(Integer idUser, Integer idMessage) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            PreparedStatement s = this.connexionBD.prepareStatement("delete from MESSAGES where idMessage=? and idUtilisateur=?");
            s.setInt(1, idMessage);
            s.setInt(2, idUser);
            int nbLigneSuppr = s.executeUpdate();
            if(nbLigneSuppr == 0){
                throw new SQLException();
            }
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Supprime un utilisateur de la base de données.
     * @param pseudo Le pseudo de l'utilisateur à supprimer.
     * @return Le pseudo de l'utilisateur supprimé.
     * @throws SQLException En cas d'erreur SQL.
     */
    public String deleteUser(String pseudo) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            int idUser = getId(pseudo);
            clearRelationShip(idUser);
            PreparedStatement s = this.connexionBD.prepareStatement("delete from UTILISATEUR where idUtilisateur=?");
            s.setInt(1, idUser);
            int nbLigneSuppr = s.executeUpdate();
            if(nbLigneSuppr == 0){
                throw new SQLException();
            }
            return pseudo;
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Supprime toutes les relations d'un utilisateur dans la base de données.
     * @param idUser L'identifiant de l'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public void clearRelationShip(Integer idUser) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            PreparedStatement s = this.connexionBD.prepareStatement("delete from FOLLOW where idUtilisateur1=? or idUtilisateur2=?");
            s.setInt(1, idUser);
            s.setInt(2, idUser);
            s.executeUpdate();
            PreparedStatement s2 = this.connexionBD.prepareStatement("delete from LIKES where idUtilisateur=?");
            s2.setInt(1, idUser);
            s2.executeUpdate();
            PreparedStatement s3 = this.connexionBD.prepareStatement("delete from MESSAGES where idUtilisateur=?");
            s3.setInt(1, idUser);
            s3.executeUpdate();
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Supprime un message de la base de données.
     * @param idMessage L'identifiant du message à supprimer.
     * @throws SQLException En cas d'erreur SQL.
     */
    public void deleteMsg(Integer idMessage) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            clearLikesMessage(idMessage);
            PreparedStatement s = this.connexionBD.prepareStatement("delete from MESSAGES where idMessage=?");
            s.setInt(1, idMessage);
            int nbLigneSuppr = s.executeUpdate();
            if(nbLigneSuppr == 0){
                throw new SQLException();
            }
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }
    
    /**
     * Supprime tous les likes associés à un message.
     * @param idMessage L'identifiant du message.
     * @throws SQLException En cas d'erreur SQL.
     */
    public void clearLikesMessage(Integer idMessage) throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            PreparedStatement s = this.connexionBD.prepareStatement("delete from LIKES where idMessage=?");
            s.setInt(1, idMessage);
            s.executeUpdate();
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }
    
    /**
     * Supprime toutes les données de la base de données.
     * @throws SQLException En cas d'erreur SQL.
     */
    public void deleteAll() throws SQLException{
        this.st = this.connexionBD.createStatement();
        try{
            PreparedStatement s = this.connexionBD.prepareStatement("delete from LIKES");
            s.executeUpdate();
            PreparedStatement s2 = this.connexionBD.prepareStatement("delete from FOLLOW");
            s2.executeUpdate();
            PreparedStatement s3 = this.connexionBD.prepareStatement("delete from MESSAGES");
            s3.executeUpdate();
            PreparedStatement s4 = this.connexionBD.prepareStatement("delete from UTILISATEUR");
            s4.executeUpdate();
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Récupère l'historique des messages d'un utilisateur.
     * @param idUser L'identifiant de l'utilisateur.
     * @param nbMessages Le nombre de messages à récupérer.
     * @return Une liste des messages de l'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public List<Message> getHistorique(int idUser, int nbMessages) throws SQLException{
        try{
            if(nbMessages <= 0){
                throw new SQLException();
            }
            this.st = this.connexionBD.createStatement();
            ResultSet rs = this.st.executeQuery("select * from MESSAGES where idUtilisateur='"+ idUser +"' order by dateEnvoiMessage desc limit "+ nbMessages);
            List<Message> messages = new ArrayList<>();
            while(rs.next()){
                int idMessage = rs.getInt(1);
                int idSender = rs.getInt(2);
                String message = rs.getString(3);
                String date = rs.getTimestamp(4).toString();
                Utilisateur sender = loadUser(idSender);
                Message msg = new Message(date, message, sender, idMessage);
                messages.add(msg);
            }
            return messages;
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Récupère la liste des utilisateurs qui suivent un utilisateur donné.
     * @param idUtilisateur L'identifiant de l'utilisateur suivi.
     * @return Une liste des utilisateurs qui suivent l'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public List<Utilisateur> getFollowers(Integer idUtilisateur) throws SQLException{
        try{
            this.st = this.connexionBD.createStatement();
            ResultSet rs = this.st.executeQuery("select idUtilisateur1 from FOLLOW where idUtilisateur2='"+ idUtilisateur +"'");
            List<Utilisateur> followers = new ArrayList<>();
            while(rs.next()){
                int idFollower = rs.getInt(1);
                Utilisateur follower = loadUser(idFollower);
                followers.add(follower);
            }
            return followers;
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Récupère la liste des utilisateurs suivis par un utilisateur donné.
     * @param idUtilisateur L'identifiant de l'utilisateur qui suit.
     * @return Une liste des utilisateurs suivis par l'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public List<Utilisateur> getFollowing(Integer idUtilisateur) throws SQLException{
        try{
            this.st = this.connexionBD.createStatement();
            ResultSet rs = this.st.executeQuery("select idUtilisateur2 from FOLLOW where idUtilisateur1='"+ idUtilisateur +"'");
            List<Utilisateur> followings = new ArrayList<>();
            while(rs.next()){
                int idFollowing = rs.getInt(1);
                Utilisateur following = loadUser(idFollowing);
                followings.add(following);
            }
            return followings;
        }
        catch(SQLException e){
            throw new SQLException();
        }
    }

    /**
     * Fonction de hachage SHA-256 pour les mots de passe.
     * @param base La chaîne à hacher.
     * @return La chaîne hachée.
     */
    public static String hash(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) 
                hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
        throw new RuntimeException(ex);
        }
    }
}