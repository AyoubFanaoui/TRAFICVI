package org.app.traficvi.model;

import java.util.*;

public class PacketAnalyzer {


    private static Set<String> alertedIps = new HashSet<>();  // Conserve les IPs déjà alertées pour DDoS

    private static final int DDOS_THRESHOLD = 100;  // Seuil pour une attaque DDoS classique
    private static final int UDP_THRESHOLD = 50;   // Seuil pour une attaque par amplification (UDP)
    private static final int SYN_FLOOD_THRESHOLD = 50;  // Seuil pour une attaque SYN flood

    /**
     * Analyse une liste de paquets pour détecter des intrusions.
     *
     * @param packets Liste de paquets à analyser.
     * @return Liste d'alertes d'intrusion détectées.
     */
    public static List<IntrusionAlert> analyzePackets(List<NetworkPacket> packets) {
        List<IntrusionAlert> alerts = new ArrayList<>();
        Map<String, Integer> packetCountByIp = new HashMap<>();
        Map<String, Integer> udpCountByIp = new HashMap<>();
        Map<String, Integer> synCountByIp = new HashMap<>();
        Set<String> alertedAlerts = new HashSet<>(); // Pour éviter les alertes redondantes (combinant IP et port)

        for (NetworkPacket packet : packets) {
            // Vérifier si cette alerte (IP source + port) a déjà été émise
            String alertKey = packet.getSourceIP() + ":" + packet.getPort();
            if (alertedAlerts.contains(alertKey)) {
                continue;  // Si l'alerte a déjà été émise, passer au paquet suivant
            }

            // Détection des attaques DDoS
            if (isDdosAttack(packet, packetCountByIp)) {
                if (!alertedIps.contains(packet.getSourceIP())) {
                    alertedIps.add(packet.getSourceIP());
                    alerts.add(new IntrusionAlert(
                            "Attaque DDoS détectée",
                            "Un grand nombre de paquets provient de la même IP : " + packet.getSourceIP(),
                            System.currentTimeMillis(),
                            packet
                    ));
                }
                continue;  // Ignore les autres vérifications une fois l'alerte DDoS déclenchée
            }

            // Détection d'une attaque par amplification (UDP)
            if (isUdpAmplification(packet, udpCountByIp)) {
                alerts.add(new IntrusionAlert(
                        "Attaque par amplification UDP détectée",
                        "Un grand nombre de paquets UDP provient de l'IP : " + packet.getSourceIP(),
                        System.currentTimeMillis(),
                        packet
                ));
                continue;  // Ignore les autres vérifications une fois l'alerte d'amplification UDP déclenchée
            }

            // Détection d'une attaque SYN flood
            if (isSynFlood(packet, synCountByIp)) {
                alerts.add(new IntrusionAlert(
                        "Attaque SYN flood détectée",
                        "Un grand nombre de paquets SYN envoyés depuis l'IP : " + packet.getSourceIP(),
                        System.currentTimeMillis(),
                        packet
                ));
                continue;  // Ignore les autres vérifications une fois l'alerte SYN flood déclenchée
            }

            // Analyse individuelle du paquet
            AnalysisResult result = analyzePacket(packet);
            if (result.isIntrusionDetected()) {
                alerts.add(new IntrusionAlert(
                        result.getDescription(),
                        result.getRecommendations(),
                        System.currentTimeMillis(),
                        packet
                ));
                alertedAlerts.add(alertKey);  // Marquer l'alerte comme émise
            }
        }

        printAlerts(alerts);

        return alerts;
    }

    /**
     * Détecte une attaque DDoS en comptant les paquets par IP source.
     *
     * @param packet              Paquet réseau à vérifier.
     * @param packetCountByIp     Carte comptant les paquets par IP source.
     * @return True si une attaque DDoS est détectée, sinon False.
     */
    private static boolean isDdosAttack(NetworkPacket packet, Map<String, Integer> packetCountByIp) {
        String sourceIp = packet.getSourceIP();
        packetCountByIp.put(sourceIp, packetCountByIp.getOrDefault(sourceIp, 0) + 1);

        // Si un certain seuil est dépassé, une alerte DDoS est générée pour cette IP
        if (packetCountByIp.get(sourceIp) > DDOS_THRESHOLD) {
            return true; // L'IP a dépassé le seuil de 100 paquets
        }

        return false;
    }

    /**
     * Détecte une attaque par amplification UDP en comptant les paquets UDP par IP source.
     *
     * @param packet              Paquet réseau à vérifier.
     * @param udpCountByIp        Carte comptant les paquets UDP par IP source.
     * @return True si une attaque par amplification UDP est détectée, sinon False.
     */
    private static boolean isUdpAmplification(NetworkPacket packet, Map<String, Integer> udpCountByIp) {
        if ("UDP".equalsIgnoreCase(packet.getProtocole())) {
            String sourceIp = packet.getSourceIP();
            udpCountByIp.put(sourceIp, udpCountByIp.getOrDefault(sourceIp, 0) + 1);

            // Si un certain seuil est dépassé pour les paquets UDP, une alerte d'amplification est générée
            if (udpCountByIp.get(sourceIp) > UDP_THRESHOLD) {
                return true;
            }
        }

        return false;
    }

    /**
     * Détecte une attaque SYN flood en comptant les paquets SYN par IP source.
     *
     * @param packet              Paquet réseau à vérifier.
     * @param synCountByIp        Carte comptant les paquets SYN par IP source.
     * @return True si une attaque SYN flood est détectée, sinon False.
     */
    private static boolean isSynFlood(NetworkPacket packet, Map<String, Integer> synCountByIp) {
        if ("TCP".equalsIgnoreCase(packet.getProtocole()) && "SYN".equalsIgnoreCase(packet.getPacketContent())) {
            String sourceIp = packet.getSourceIP();
            synCountByIp.put(sourceIp, synCountByIp.getOrDefault(sourceIp, 0) + 1);

            // Si un certain seuil est dépassé pour les paquets SYN, une alerte SYN flood est générée
            if (synCountByIp.get(sourceIp) > SYN_FLOOD_THRESHOLD) {
                return true;
            }
        }

        return false;
    }

    /**
     * Analyse un paquet pour détecter des comportements suspects.
     *
     * @param packet Paquet réseau à analyser.
     * @return Résultat de l'analyse sous forme d'AnalysisResult.
     */
    public static AnalysisResult analyzePacket(NetworkPacket packet) {
        if (packet == null) {
            return new AnalysisResult(false, "Paquet invalide", "Vérifiez la source.", null);
        }

        // Vérifier si l'adresse source ou l'adresse de destination est manquante
        if (packet.getSourceIP().equals("Not an IP packet") || packet.getDestinationIP().equals("Not an IP packet")) {
            return new AnalysisResult(
                    true,
                    "Paquet incomplet détecté",
                    "L'adresse source ou de destination est manquante.",
                    packet.getPort()
            );
        }

        // Détection de paquets volumineux
        if (packet.getSize() > 1000000) {
            return new AnalysisResult(
                    true,
                    "Paquet volumineux détecté",
                    "Inspectez la source IP : " + packet.getSourceIP(),
                    packet.getPort()
            );
        }

        // Détection de contenu sensible
        if (packet.getPacketContent().toLowerCase().contains("mot de passe")) {
            return new AnalysisResult(
                    true,
                    "Contenu sensible détecté",
                    "Vérifiez les données transmises depuis : " + packet.getSourceIP(),
                    packet.getPort()
            );
        }

        // Détection d'un port suspect (par exemple, port 23 utilisé pour Telnet)
        if (packet.getPort() == 23) {
            return new AnalysisResult(
                    true,
                    "Port suspect détecté",
                    "Vérifiez l'utilisation du port 23 (Telnet) : " + packet.getSourceIP(),
                    packet.getPort()
            );
        }

        // Si aucune intrusion n'est détectée
        return new AnalysisResult(false, "Pas d'intrusion détectée", "Aucune action nécessaire.", packet.getPort());
    }

    /**
     * Affiche les alertes détectées.
     *
     * @param alerts Liste des alertes à afficher.
     */
    public static void printAlerts(List<IntrusionAlert> alerts) {
        for (IntrusionAlert alert : alerts) {
            System.out.println("Alerte :");
            System.out.println("Type : " + alert.getType());
            System.out.println("Intrusion : " + alert.getIntrusion());
            System.out.println("Timestamp : " + alert.getTimestamp());
            System.out.println("Paquet : " + alert.getPacket().getPacketInfo());
            System.out.println("----------------------------------");
        }
    }

    /**
     * Classe interne représentant le résultat d'une analyse.
     */
    public static class AnalysisResult {
        private final boolean intrusionDetected;
        private final String description;
        private final String recommendations;
        private final Integer port;

        public AnalysisResult(boolean intrusionDetected, String description, String recommendations, Integer port) {
            this.intrusionDetected = intrusionDetected;
            this.description = description;
            this.recommendations = recommendations;
            this.port = port;
        }

        public boolean isIntrusionDetected() {
            return intrusionDetected;
        }

        public String getDescription() {
            return description;
        }

        public String getRecommendations() {
            return recommendations;
        }

        public Integer getPort() {
            return port;
        }
    }

    /**
     * Méthode principale pour tester l'analyse des paquets.
     */
    public static void main(String[] args) {
        List<NetworkPacket> packets = new ArrayList<>();

        // Ajout de paquets de test
        packets.add(new NetworkPacket("Not an IP packet", "192.168.0.2", 500, "2025-01-21 12:00:00", "TCP",80 ,"Données normales"));
        packets.add(new NetworkPacket("192.168.0.3", "192.168.0.4", 2500000, "2025-01-21 12:00:05", "TCP",80 ,"Fichier volumineux suspect"));
        packets.add(new NetworkPacket("192.168.0.5", "192.168.0.6", 400, "2025-01-21 12:00:10", "FTP",80 ,"Protocole FTP détecté"));

        // Simulation d'une attaque DDoS
        for (int i = 0; i < 110; i++) {
            packets.add(new NetworkPacket("192.168.1.1", "192.168.0.2", 100, "2025-01-21 12:00:15", "HTTP",80 ,"Données répétées DDoS"));
        }

        // Analyser les paquets
        List<IntrusionAlert> alerts = analyzePackets(packets);

        // Afficher les alertes
        printAlerts(alerts);
    }
}
