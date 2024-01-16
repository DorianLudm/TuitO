import java.sql.*;

public class ConnexionBD {
	private Connection mysql=null;
	private boolean connecte=false;

	public ConnexionBD() throws ClassNotFoundException{
		Class.forName("org.mariadb.jdbc.Driver");
	}

	public void connecter() throws SQLException {
		//this.mysql = DriverManager.getConnection("jdbc:mysql://servinfo-maria:3306/DBludmann", "ludmann", "ludmann"); //IUT
		this.mysql = DriverManager.getConnection("jdbc:mariadb://localhost:3306/Dorian", "dorian", "ludmann"); //Local PC
		this.connecte=this.mysql!=null;
    }
	
	public void close() throws SQLException {
		// fermer la connexion
		this.connecte=false;
	}

    public boolean isConnecte() {return this.connecte;}
	
	public Statement createStatement() throws SQLException {
		return this.mysql.createStatement();
	}

	public PreparedStatement prepareStatement(String requete) throws SQLException{
		return this.mysql.prepareStatement(requete);
	}
	
	public Connection getCon(){
        return this.mysql;
    }
}