import java.sql.*;

/**
 * La classe ConnexionBD gère la connexion à la base de données MariaDB.
 */
public class ConnexionBD {
	private Connection mysql=null;
	private boolean connecte=false;
	private DatabaseLogins logins;

	/**
     * Constructeur de la classe ConnexionBD. Initialise la connexion à la base de données MariaDB.
     * @throws ClassNotFoundException Si la classe du pilote de base de données n'est pas trouvée.
     */
	public ConnexionBD() throws ClassNotFoundException{
		Class.forName("org.mariadb.jdbc.Driver");
		this.logins = DatabaseLogins.getInstance();
	}

	/**
     * Établit la connexion à la base de données MariaDB.
     * @throws SQLException En cas d'erreur lors de la connexion à la base de données.
     */
	public void connecter() throws SQLException {
		//this.mysql = DriverManager.getConnection("jdbc:mysql://servinfo-maria:3306/DBludmann", "ludmann", "ludmann"); //IUT
		this.mysql = DriverManager.getConnection("jdbc:mariadb://localhost:3306/" + logins.getNomBD(), logins.getNomUtilisateurSQL(), logins.getMotDePasseSQL()); //Local PC
		this.connecte=this.mysql!=null;
    }
	
	/**
     * Ferme la connexion à la base de données MariaDB.
     * @throws SQLException En cas d'erreur lors de la fermeture de la connexion.
     */
	public void close() throws SQLException {
		// fermer la connexion
		this.connecte=false;
	}

	/**
     * Vérifie si la connexion à la base de données est établie.
     * @return Vrai si la connexion est établie, faux sinon.
     */
    public boolean isConnecte() {return this.connecte;}
	
	/**
     * Crée et renvoie un objet Statement pour l'exécution de requêtes SQL.
     * @return Un objet Statement.
     * @throws SQLException En cas d'erreur lors de la création de l'objet Statement.
     */
	public Statement createStatement() throws SQLException {
		return this.mysql.createStatement();
	}

	/**
     * Prépare une requête SQL pour l'exécution et renvoie un objet PreparedStatement.
     * @param requete La requête SQL à préparer.
     * @return Un objet PreparedStatement prêt à être exécuté.
     * @throws SQLException En cas d'erreur lors de la préparation de la requête.
     */
	public PreparedStatement prepareStatement(String requete) throws SQLException{
		return this.mysql.prepareStatement(requete);
	}
	
	/**
     * Obtient l'objet Connection représentant la connexion à la base de données.
     * @return L'objet Connection représentant la connexion à la base de données.
     */
	public Connection getCon(){
        return this.mysql;
    }
}