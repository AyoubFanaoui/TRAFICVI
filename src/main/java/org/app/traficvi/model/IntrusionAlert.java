package org.app.traficvi.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IntrusionAlert {
    //public static final String ANSI_BLACK = "\u001B[36m";
    private NetworkPacket packet;
    private String type;
    private String Intrusion;
    private long timestamp;
    private String dangerLevel;
    private String recommendations;

    public IntrusionAlert(String type, String Intrusion, long timesstamp, NetworkPacket packet) {
        this.type = type;
        this.Intrusion = Intrusion;
        this.timestamp = timesstamp;
        this.packet = packet;
        this.dangerLevel = dangerLevel;
        this.recommendations = "";
    }
    public IntrusionAlert(String type, String Intrusion, long timesstamp, NetworkPacket packet, String dangerLevel) {
        this.type = type;
        this.Intrusion = Intrusion;
        this.timestamp = timesstamp;
        this.packet = packet;
        this.dangerLevel = dangerLevel;
        this.recommendations = "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntrusion() {
        return Intrusion;
    }

    public void setIntrusion(String Intrusion) {
        this.Intrusion = Intrusion;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public String getTimestampString(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(new Date(timestamp));
        return formattedTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public NetworkPacket getPacket() {
        return packet;
    }
    public String getDangerLevel() {return dangerLevel;}
    public void setDangerLevel(String dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    // Getters and Setters pour recommendations
    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    // Simulation de l'envoi de notification
    /*public void sendNotification() {
        System.out.println("Notification envoyée: [Type: " + type + ", Détails: " + details + ", Timestamp: " + timestamp + "]");
    }*/


}
