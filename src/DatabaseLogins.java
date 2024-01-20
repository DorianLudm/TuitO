import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * La classe DatabaseLogins gère les informations de connexion à la base de données.
 */
public class DatabaseLogins {
    private String nomBD;
    private String nomUtilisateurSQL;
    private String motDePasseSQL;

    private static DatabaseLogins instance;

    /**
     * Constructeur privé de la classe DatabaseLogins.
     * @param nomBD Le nom de la base de données.
     * @param nomUtilisateurSQL Le nom d'utilisateur SQL.
     * @param motDePasseSQL Le mot de passe SQL.
     */
    private DatabaseLogins(String nomBD, String nomUtilisateurSQL, String motDePasseSQL){
        this.nomBD = nomBD;
        this.nomUtilisateurSQL = nomUtilisateurSQL;
        this.motDePasseSQL = motDePasseSQL;
    }

    /**
     * Obtient le nom de la base de données.
     * @return Le nom de la base de données.
     */
    public String getNomBD(){
        return this.nomBD;
    }

    /**
     * Obtient le nom d'utilisateur SQL.
     * @return Le nom d'utilisateur SQL.
     */
    public String getNomUtilisateurSQL(){
        return this.nomUtilisateurSQL;
    }

    /**
     * Obtient le mot de passe SQL.
     * @return Le mot de passe SQL.
     */
    public String getMotDePasseSQL(){
        return this.motDePasseSQL;
    }

    /**
     * Obtient l'instance unique de la classe DatabaseLogins en utilisant le modèle Singleton.
     * Lit les informations de connexion à partir d'un fichier de configuration.
     * @return L'instance unique de DatabaseLogins.
     */

    public static DatabaseLogins getInstance(){
        if (DatabaseLogins.instance == null) {
            try {
                File file = new File("configBD.txt");
                Scanner scanner = new Scanner(file);
                for (int i = 0; i < 8; i++) {
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                }
                String nomBD = scanner.hasNextLine() ? scanner.nextLine() : "";
                String nomUtilisateurSQL = scanner.hasNextLine() ? scanner.nextLine() : "";
                String motDePasseSQL = scanner.hasNextLine() ? scanner.nextLine() : "";
                DatabaseLogins.instance = new DatabaseLogins(nomBD, nomUtilisateurSQL, motDePasseSQL);
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return DatabaseLogins.instance;
    }
}