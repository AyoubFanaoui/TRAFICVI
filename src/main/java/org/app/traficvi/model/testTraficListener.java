package org.app.traficvi.model;


import org.pcap4j.core.PcapNetworkInterface;

import java.util.*;

public class testTraficListener {
    public static void main(String[] args) {
        TrafficListener trafficListener = new TrafficListener();

        // Étape 1 : Liste des interfaces réseau disponibles
        System.out.println("Récupération des interfaces réseau disponibles...");
        List<PcapNetworkInterface> interfaces = trafficListener.NetworkInterface();

        if (interfaces.isEmpty()) {
            System.out.println("Au6cune interface réseau disponible. Programme terminé.");
            return;
        }


        // Affichage des interfaces disponibles
        System.out.println("Interfaces réseau disponibles :");
        for (int i = 0; i < interfaces.size(); i++) {
            System.out.println((i + 1) + ". " + interfaces.get(i).getName() + " - " + interfaces.get(i).getDescription());
        }

        // Étape 2 : Sélection d'une interface par l'utilisateur
        Scanner scanner = new Scanner(System.in);
        System.out.println("Veuillez sélectionner une interface réseau (entrez le numéro correspondant) :");
        int choice = -1;
        while (choice < 1 || choice > interfaces.size()) {
            System.out.print("Choix : ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1 || choice > interfaces.size()) {
                    System.out.println("Choix invalide. Veuillez réessayer.");
                }
            } else {
                System.out.println("Veuillez entrer un numéro valide.");
                scanner.next(); // Consommer l'entrée non valide
            }
        }

        PcapNetworkInterface selectedInterface = interfaces.get(choice - 1);
        System.out.println("Interface sélectionnée : " + selectedInterface.getName());

        // Étape 3 : Capture des paquets sur l'interface sélectionnée
        System.out.println("Début de la capture des paquets...");
        List<NetworkPacket> capturedPackets = trafficListener.captureTrafic(selectedInterface);

        // Affichage des paquets capturés
        System.out.println("Résultats de la capture :");
        if (capturedPackets.isEmpty()) {
            System.out.println("Aucun paquet capturé.");
        } else {
            for (NetworkPacket packet : capturedPackets) {
                System.out.println(packet.getPacketInfo());
            }
        }

        System.out.println("Programme terminé.");
    }
}
