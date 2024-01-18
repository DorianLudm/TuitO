import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseManager {
    private ConnexionBD connexionBD;
    private Statement st;

    public DatabaseManager(ConnexionBD connexion){
        this.connexionBD = connexion;
    }

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

    public int newIdUser() throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select max(idUtilisateur) from UTILISATEUR;");
        if (rs.next()){
            int maxint = rs.getInt(1);
            return (maxint+1);
        }
        return 1;
    }

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

    public int newIdMessage() throws SQLException{
        this.st = this.connexionBD.createStatement();
        ResultSet rs = this.st.executeQuery("select max(idMessage) from MESSAGES;");
        if (rs.next()){
            int maxint = rs.getInt(1);
            return (maxint+1);
        }
        return 1;
    }

    public List<Integer> getFollowers(int idSender) throws SQLException{
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