import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;

/**
 * Représente un message dans le système.
 */
public class Message {
    private String dateEnvoi;
    private String message;
    private Utilisateur sender;
    private Integer idMessage;

    /**
     * Constructeur d'un nouveau message avec une date automatique.
     * @param message Le contenu du message.
     * @param sender L'utilisateur qui envoie le message.
     */
    public Message(String message, Utilisateur sender){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        this.dateEnvoi = dtf.format(localDateTime);
        this.message = message;
        this.sender = sender;
    }

    /**
     * Constructeur d'un message avec une date automatique et un identifiant.
     * @param message Le contenu du message.
     * @param sender L'utilisateur qui envoie le message.
     * @param idMessage L'identifiant du message.
     */
    public Message(String message, Utilisateur sender, Integer idMessage){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        this.dateEnvoi = dtf.format(localDateTime);
        this.message = message;
        this.sender = sender;
        this.idMessage = idMessage;
    }

    /**
     * Constructeur d'un message avec une date spécifiée, un contenu, un expéditeur et un identifiant.
     * @param dateEnvoie La date d'envoi du message.
     * @param message Le contenu du message.
     * @param sender L'utilisateur qui envoie le message.
     * @param idMessage L'identifiant du message.
     */
    public Message(String dateEnvoie, String message, Utilisateur sender, Integer idMessage){
        this.dateEnvoi = dateEnvoie;
        this.message = message;
        this.sender = sender;
        this.idMessage = idMessage;
    }

    /**
     * Récupère la date d'envoi du message.
     * @return La date d'envoi du message.
     */
    public String getDate(){
        return this.dateEnvoi;
    }

    /**
     * Récupère le contenu du message.
     * @return Le contenu du message.
     */
    public String getMessage(){
        return this.message;
    }

    /**
     * Récupère l'expéditeur du message.
     * @return L'expéditeur du message.
     */
    public Utilisateur getSender(){
        return this.sender;
    }

    /**
     * Récupère l'identifiant du message.
     * @return L'identifiant du message.
     */
    public Integer getIdMessage(){
        return this.idMessage;
    }
    
    /**
     * Formate le message pour l'affichage.
     * @return Une chaîne formatée représentant le message.
     */
    public String formatMessage(){
        String[] dateTime = this.dateEnvoi.split(" ");
        String date = dateTime[0];
        String time = dateTime[1];
        return "ID Message: " + (this.idMessage != null ? this.idMessage : "N/A") + " - " + this.sender.getPseudo() + " a Tuité à " + time + " le " + date + " - " + this.message;
    }
    
    /**
     * Convertit l'objet en chaîne JSON.
     * @return Une représentation JSON de l'objet.
     */
    @Override
    public String toString() {
        Gson gson = new Gson();
        return "{ \"dateEnvoi\": \"" + this.dateEnvoi + "\", \"message\": \"" + this.message + "\", \"sender\": " + gson.toJson(this.sender) + " }";
    }

    /**
     * Convertit l'objet en chaîne JSON avec un identifiant de message spécifié.
     * @param idMessage L'identifiant de message à inclure.
     * @return Une représentation JSON de l'objet avec l'identifiant de message.
     */
    public String toString(int idMessage) {
        Gson gson = new Gson();
        return "{ \"idMessage\": " + idMessage + ", \"dateEnvoi\": \"" + this.dateEnvoi + "\", \"message\": \"" + this.message + "\", \"sender\": " + gson.toJson(this.sender) + " }";
    }
}
