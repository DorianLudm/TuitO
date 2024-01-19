import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DatabaseLogins {
    private String nomBD;
    private String nomUtilisateurSQL;
    private String motDePasseSQL;

    private static DatabaseLogins instance;

    private DatabaseLogins(String nomBD, String nomUtilisateurSQL, String motDePasseSQL){
        this.nomBD = nomBD;
        this.nomUtilisateurSQL = nomUtilisateurSQL;
        this.motDePasseSQL = motDePasseSQL;
    }

    public String getNomBD(){
        return this.nomBD;
    }

    public String getNomUtilisateurSQL(){
        return this.nomUtilisateurSQL;
    }

    public String getMotDePasseSQL(){
        return this.motDePasseSQL;
    }

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