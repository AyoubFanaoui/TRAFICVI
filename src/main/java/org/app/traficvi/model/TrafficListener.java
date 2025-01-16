package org.app.traficvi.model;


import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class TrafficListener implements TrafficCapture{
    private List<NetworkPacket> capturedPackets = new ArrayList<>();

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
    public List<NetworkPacket> captureTrafic( PcapNetworkInterface  networkInterface ){

        try{
            System.out.println("Interface sélectionnée : " + networkInterface.getName());


            //Creation d'un handle pour capture
            int snapshotLength = 65536;// Longueur maximale du paquet à capturer
            int timeout = 10; // Timeout en millisecondes
            PcapHandle handle = networkInterface.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, timeout);

            // Capture des paquets

            System.out.println("Capture des paquets en cours...");
            handle.loop(10, (PacketListener) packet -> {
                //System.out.println("I'am here");
                NetworkPacket networkPacket = convertToNetworkPacket(packet);
                capturedPackets.add(networkPacket);
                //System.out.println(networkPacket.getPacketInfo());

            });
            // Fermer le handle
            handle.close();


        }catch (Exception e) {
            System.err.println("Erreur pendant la capture : " + e.getMessage());
        }
        return capturedPackets;
    }

    private NetworkPacket convertToNetworkPacket(Packet packet) {
        // Par défaut, les adresses IP sont "N/A"
        String sourceIP = "N/A";
        String destinationIP = "N/A";

        // Vérifier si le paquet contient une couche IPv4
        if (packet.contains(IpV4Packet.class)) {
            IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
            sourceIP = ipV4Packet.getHeader().getSrcAddr().getHostAddress();
            destinationIP = ipV4Packet.getHeader().getDstAddr().getHostAddress();
        }
        // Vérifier si le paquet contient une couche IPv6
        else if (packet.contains(IpV6Packet.class)) {
            IpV6Packet ipV6Packet = packet.get(IpV6Packet.class);
            sourceIP = ipV6Packet.getHeader().getSrcAddr().getHostAddress();
            destinationIP = ipV6Packet.getHeader().getDstAddr().getHostAddress();
        }

        // Taille du paquet
        int size = packet.length();

        // Timestamp de capture
        long timestamp = System.currentTimeMillis();

        // Retourner une instance de NetworkPacket avec les informations extraites
        return new NetworkPacket(sourceIP, destinationIP, size, timestamp);
    }


}
