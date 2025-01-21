package org.app.traficvi.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class NetworkPacket {
    private final SimpleStringProperty sourceIP;
    private final SimpleStringProperty destinationIP;
    private final SimpleLongProperty size;
    private final SimpleStringProperty protocole;
    private final SimpleIntegerProperty port;
    private final SimpleStringProperty PacketContent;
    private final SimpleStringProperty timestamp;



    public NetworkPacket(String sourceIP, String destinationIP, int size,  String timestamp, String protocole,Integer port ,String packetContent) {
        this.sourceIP = new SimpleStringProperty(sourceIP);
        this.destinationIP = new SimpleStringProperty(destinationIP);
        this.size = new SimpleLongProperty(size);
        this.timestamp = new SimpleStringProperty(timestamp);
        this.protocole = new SimpleStringProperty(protocole);
        this.PacketContent = new SimpleStringProperty(packetContent);
        this.port = new SimpleIntegerProperty(port);
    }

    public String getSourceIP() {
        return sourceIP.get();
    }

    public String getDestinationIP() {
        return destinationIP.get();
    }

    public long getSize() {
        return size.get();
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public String getProtocole() {
        return protocole.get();
    }
    public String getPacketContent() {
        return PacketContent.get();
    }

    public Integer getPort() {
        return port.get();
    }



    public String getPacketInfo() {
        return "Source: " + sourceIP + ", Destination: "+ destinationIP+", Taille: "+ size+"octets , Timestamp: "+ timestamp;
    }


}