package org.app.traficvi.model;

public class IntrusionAlert {
    public static final String ANSI_BLACK = "\u001B[36m";
    private String type;
    private String details;
    private long timestamp;

    public IntrusionAlert(String type, String details, long timesstamp) {
        this.type = type;
        this.details = details;
        this.timestamp = timesstamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Simulation de l'envoi de notification
    public void sendNotification() {
        System.out.println("Notification envoyée: [Type: " + type + ", Détails: " + details + ", Timestamp: " + timestamp + "]");
    }

    //test
    public static void main(String[] args) {
        // Créer un objet IntrusionAlert
        IntrusionAlert alert = new IntrusionAlert("DoS Attack", "Tentative de saturation du réseau", System.currentTimeMillis());

        // Affichage des informations de l'alerte
        System.out.println(ANSI_BLACK+"Type d'alerte: " + alert.getType()+ANSI_BLACK);
        System.out.println(ANSI_BLACK+"Détails: " + alert.getDetails()+ANSI_BLACK);
        System.out.println(ANSI_BLACK+"Timestamp: " + alert.getTimestamp()+ANSI_BLACK);

        // Tester l'envoi de la notification
        alert.sendNotification();
    }
}
