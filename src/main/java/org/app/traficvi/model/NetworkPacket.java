package org.app.traficvi.model;

public class NetworkPacket {
    private String sourcIP;
    private String destinationIP;
    private int size;
    private long timestamp;



    public NetworkPacket(String sourcIP, String destinationIP, int size, long timestamp) {
        this.sourcIP = sourcIP;
        this.destinationIP = destinationIP;
        this.size = size;
        this.timestamp = timestamp;
    }

    public String getSourcIP() {
        return sourcIP;
    }

    public void setSourcIP(String sourcIP) {
        this.sourcIP = sourcIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPacketInfo() {
        return "Source: " + sourcIP + ", Destination: "+ destinationIP+", Taille: "+ size+"octets , Timestamp: "+ timestamp;
    }
}
