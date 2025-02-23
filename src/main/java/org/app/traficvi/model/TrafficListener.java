package org.app.traficvi.model;

import javafx.application.Platform;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.IpVersion;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class TrafficListener implements TrafficCapture {
    private final Consumer<NetworkPacket> updateCallback;
    private final Consumer<IntrusionAlert> alertCallback;
    private Runnable statsUpdateCallback ;

    private List<NetworkPacket> capturedPackets = new ArrayList<>();
    private List<IntrusionAlert> alerts = new ArrayList<>();

    public TrafficListener(Consumer<NetworkPacket> updateCallback, Runnable statsUpdateCallback,  Consumer<IntrusionAlert> alertCallback) {
        this.updateCallback = updateCallback;
        this.statsUpdateCallback = statsUpdateCallback;
        this.alertCallback = alertCallback;

    }

    @Override
    public List<PcapNetworkInterface> NetworkInterface() {
        try {
            List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
            if (interfaces == null || interfaces.isEmpty()) {
                System.err.println("Aucune interface réseau disponible !");
                return new ArrayList<>();
            }
            return interfaces;
        } catch (PcapNativeException e) {
            System.err.println("Erreur lors de la récupération des interfaces réseau : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<NetworkPacket> captureTrafic(PcapNetworkInterface networkInterface) {
        try {
            //System.out.println("Interface sélectionnée : " + networkInterface.getName());

            int snapshotLength = 65536;
            int timeout = 10;



            PcapHandle handle = null;
            try {
                handle = networkInterface.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, timeout);

                System.out.println("Capture des paquets en cours...");
                handle.loop(-1, (PacketListener) packet -> {
                    NetworkPacket networkPacket = convertToNetworkPacket(packet);
                    //fiture ajoute selemnts les packet probleme
                    capturedPackets.add(networkPacket);


                    if(capturedPackets.size() >= 10) {
                        // Ajout de paquets de test
                        capturedPackets.add(new NetworkPacket("Not an IP packet", "192.168.0.2", 500, "2025-01-21 12:00:00", "TCP",80 ,"Données normales"));
                        capturedPackets.add(new NetworkPacket("192.168.0.3", "192.168.0.4", 2500000, "2025-01-21 12:00:05", "TCP",80 ,"Fichier volumineux suspect"));
                        capturedPackets.add(new NetworkPacket("192.168.0.5", "192.168.0.6", 400, "2025-01-21 12:00:10", "FTP",80 ,"Protocole FTP détecté"));

                        // Simulation d'une attaque DDoS
                        for (int i = 0; i < 110; i++) {
                            capturedPackets.add(new NetworkPacket("192.168.1.1", "192.168.0.2", 100, "2025-01-21 12:00:15", "HTTP",80 ,"Données répétées DDoS"));
                        }
                    }


                    // Analyser le paquet avec PacketAnalyzer

                        List<IntrusionAlert> alerts = PacketAnalyzer.analyzePackets(capturedPackets);

                        // Si des alertes sont détectées, les envoyer au contrôleur pour affichage
                        if (!alerts.isEmpty()) {

                            for (IntrusionAlert alert : alerts) {
                                // Notifier l'interface utilisateur des alertes

                                if (alertCallback != null) {
                                    Platform.runLater(() -> alertCallback.accept(alert));
                                }
                            }
                        }
                        if (capturedPackets.size() >= 10){
                            capturedPackets.clear();
                        }
                    //#######







                    // Mise à jour des statistiques
                    NetworkStatistics.IncrementTotalConnections();
                    NetworkStatistics.setTotalDataTransferred(
                            NetworkStatistics.getTotalDataTransferred() + networkPacket.getSize()
                    );


                    // Notifier l'interface utilisateur des nouvelles statistiques
                    if (statsUpdateCallback != null) {
                        Platform.runLater(statsUpdateCallback);
                    }

                    // Mise à jour de l'interface utilisateur
                    if (updateCallback != null) {
                        Platform.runLater(() ->{
                            updateCallback.accept(networkPacket);
                                }
                        );
                    }
                   // System.out.println("Paquet capturé : " + networkPacket.getPacketInfo());
                });
            } catch (Exception e) {
                System.err.println("Erreur pendant la capture : " + e.getMessage());
            } finally {
                if (handle != null) {
                    handle.close();
                }
            }



        } catch (Exception e) {
            System.err.println("Erreur pendant la capture : " + e.getMessage());
        }
        return capturedPackets;
    }

    public NetworkPacket convertToNetworkPacket(Packet packet) {
        // Extraire les adresses IP source et destination
        String sourceIP = extractSourceIP(packet);
        String destinationIP = extractDestinationIP(packet);
        String protocole = extractProtocol(packet) ;
        int port = extractPacketPort(packet);
        String PacketContent = extractPacketContent(packet);

        // Extraire la taille du paquet
        int taille = packet.length();

        // Obtenir l'horodatage actuel et le formater sous forme de chaîne
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Retourner le paquet sous forme de NetworkPacket
        return new NetworkPacket(sourceIP, destinationIP, taille, timestamp, protocole, port ,PacketContent);
    }

    // Méthodes pour extraire les IP (logique à adapter selon vos besoins)
    public static String extractSourceIP(Packet packet) {
        if (packet == null) {
            return "No packet data";
        }

        // Vérifier si le paquet est un paquet IP (IPv4 ou IPv6)
        if (packet.contains(IpPacket.class)) {
            IpPacket ipPacket = packet.get(IpPacket.class);

            // Identifier la version IP et retourner l'adresse IP source
            IpVersion version = ipPacket.getHeader().getVersion();
            if (version == IpVersion.IPV4) {
                return ipPacket.getHeader().getSrcAddr().getHostAddress();
            } else if (version == IpVersion.IPV6) {
                return ipPacket.getHeader().getSrcAddr().getHostAddress();
            }
        }

        return "Not an IP packet";
    }

    public static String extractDestinationIP(Packet packet) {
        if (packet == null) {
            return "No packet data";
        }

        // Vérifier si le paquet est un paquet IP (IPv4 ou IPv6)
        if (packet.contains(IpPacket.class)) {
            IpPacket ipPacket = packet.get(IpPacket.class);

            // Identifier la version IP et retourner l'adresse IP de destination
            IpVersion version = ipPacket.getHeader().getVersion();
            if (version == IpVersion.IPV4) {
                return ipPacket.getHeader().getDstAddr().getHostAddress();
            } else if (version == IpVersion.IPV6) {
                return ipPacket.getHeader().getDstAddr().getHostAddress();
            }
        }

        return "Not an IP packet";
    }

    public static String extractProtocol(Packet packet) {
        if (packet == null) {
            return "Unknown (null packet)";
        }

        // Vérifier les couches successives pour identifier le protocole
        if (packet.contains(IpPacket.class)) {
            IpPacket ipPacket = packet.get(IpPacket.class);
            if (ipPacket.contains(TcpPacket.class)) {
                return "TCP";
            } else if (ipPacket.contains(UdpPacket.class)) {
                return "UDP";
            } else if (ipPacket.contains(IcmpV4CommonPacket.class)) {
                return "ICMPv4";
            } else if (ipPacket.contains(IcmpV6CommonPacket.class)) {
                return "ICMPv6";
            } else if (ipPacket.contains(ArpPacket.class)) {
                return "ARP";
            } else if (ipPacket.contains(IpV6Packet.class)) {
                return "IPv6";
            } else if (ipPacket.contains(PppPacket.class)) {
                return "PPP (Point-to-Point Protocol)";
            } else {
                return "Unknown IP Protocol";
            }
        } else if (packet.contains(ArpPacket.class)) {
            return "ARP";
        } else if (packet.contains(EthernetPacket.class)) {
            return "Ethernet";
        } else if (packet.contains(PppPacket.class)) {
            return "PPP (Point-to-Point Protocol)";
        } else {
            return "Unknown Protocol";
        }
    }

    public static String extractPacketContent(Packet packet) {
        if (packet == null) {
            return "No packet data";
        }

        // Pour les paquets IP
        if (packet.contains(IpPacket.class)) {
            IpPacket ipPacket = packet.get(IpPacket.class);

            // Si c'est un paquet TCP ou UDP, on peut essayer de lire les données payload
            if (ipPacket.contains(TcpPacket.class)) {
                TcpPacket tcpPacket = ipPacket.get(TcpPacket.class);
                return "TCP Payload: " + Arrays.toString(tcpPacket.getPayload().getRawData());
            } else if (ipPacket.contains(UdpPacket.class)) {
                UdpPacket udpPacket = ipPacket.get(UdpPacket.class);
                return "UDP Payload: " + Arrays.toString(udpPacket.getPayload().getRawData());
            } else {
                return "IP Packet without Payload";
            }
        }

        // Pour les paquets ARP, Ethernet, ou PPP, on ne peut pas obtenir directement un contenu utile
        if (packet.contains(ArpPacket.class)) {
            return "ARP Packet (no payload)";
        }

        if (packet.contains(EthernetPacket.class)) {
            return "Ethernet Packet (no payload)";
        }

        if (packet.contains(PppPacket.class)) {
            return "PPP Packet (no payload)";
        }

        return "Unknown Packet Type";
    }

    public static int extractPacketPort(Packet packet) {
        if (packet == null) {
            return -1; // Si le paquet est null, retournez -1
        }

        // Vérifiez si le paquet contient un segment TCP
        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = packet.get(TcpPacket.class);
            return tcpPacket.getHeader().getDstPort().value(); // Retourne le port de destination
        }

        // Vérifiez si le paquet contient un segment UDP
        if (packet.contains(UdpPacket.class)) {
            UdpPacket udpPacket = packet.get(UdpPacket.class);
            return udpPacket.getHeader().getDstPort().value(); // Retourne le port de destination
        }

        // Si le paquet n'est ni TCP ni UDP, retourner -1
        return -1;
    }

    public List<IntrusionAlert> getAlerts() {
        return alerts;
    }
}
