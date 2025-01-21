package org.app.traficvi.model;

import java.util.HashMap;
import java.util.Map;



public class NetworkStatistics {
    private static int totalConnections = 0;
    private static long totalDataTransferred = 0;
    //private Map<String, Integer> activeConnections;



    public static int getTotalConnections() {
        return totalConnections;
    }

    public static void IncrementTotalConnections() {
        totalConnections = totalConnections + 1;
    }

    public static long getTotalDataTransferred() {
        return totalDataTransferred;
    }

    public static void setTotalDataTransferred(long totalDataTransferred) {
        NetworkStatistics.totalDataTransferred = totalDataTransferred;
    }






    // Affichage des statistiques
    public static void displayStats() {
        System.out.println("Total des connexions: " + totalConnections);
        System.out.println("Total des données transférées: " + totalDataTransferred + " octets");
    }



}
