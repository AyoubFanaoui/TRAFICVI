package org.app.traficvi.model;

import java.util.HashMap;
import java.util.Map;

public class NetworkStatistics {
    private int totalConnections;
    private long totalDataTransferred;
    private Map<String, Integer> activeConnections;

    public NetworkStatistics() {
        this.totalConnections = 0;
        this.totalDataTransferred = 0;
        this.activeConnections = new HashMap<>();
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public void setTotalConnections(int totalConnections) {
        this.totalConnections = totalConnections;
    }

    public long getTotalDataTransferred() {
        return totalDataTransferred;
    }

    public void setTotalDataTransferred(long totalDataTransferred) {
        this.totalDataTransferred = totalDataTransferred;
    }

    public Map<String, Integer> getActiveConnections() {
        return activeConnections;
    }

    public void setActiveConnections(Map<String, Integer> activeConnections) {
        this.activeConnections = activeConnections;
    }

    // Mettre à jour les statistiques après chaque paquet capturé
    public void updateStats(NetworkPacket packet) {
        // Augmenter le nombre total de connexions
        totalConnections++;

        // Ajouter la taille des données transférées
        totalDataTransferred += packet.getSize();

        // Mettre à jour les connexions actives en fonction de l'adresse IP source
        String sourceIP = packet.getSourcIP();
        activeConnections.put(sourceIP, activeConnections.getOrDefault(sourceIP, 0) + 1);
    }

    // Affichage des statistiques
    public void displayStats() {
        System.out.println("Total des connexions: " + totalConnections);
        System.out.println("Total des données transférées: " + totalDataTransferred + " octets");
        System.out.println("Connexions actives: " + activeConnections);
    }

    public static void main(String[] args) {
        // Créer un objet NetworkStatistics
        NetworkStatistics stats = new NetworkStatistics();

        // Simuler l'ajout de paquets
        NetworkPacket packet1 = new NetworkPacket("192.168.1.1", "192.168.1.2", 500, System.currentTimeMillis());
        NetworkPacket packet2 = new NetworkPacket("192.168.1.3", "192.168.1.2", 700, System.currentTimeMillis());
        NetworkPacket packet3 = new NetworkPacket("192.168.1.1", "192.168.1.4", 300, System.currentTimeMillis());

        // Mettre à jour les statistiques après chaque paquet
        stats.updateStats(packet1);
        stats.updateStats(packet2);
        stats.updateStats(packet3);

        // Afficher les statistiques mises à jour
        stats.displayStats();
    }

}
