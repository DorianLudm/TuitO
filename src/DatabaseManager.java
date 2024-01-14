import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        throw new SQLException();
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
            //TODO : load messages, likes, etc...
            return user;
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