package org.app.traficvi.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.app.traficvi.model.IntrusionAlert;

public class AlertController {

    private final ObservableList<IntrusionAlert> alertList;

    public AlertController(ObservableList<IntrusionAlert> alertList) {
        this.alertList = alertList;
    }

    public void updateAlertTable(IntrusionAlert alert) {
        if (alert.getType().equalsIgnoreCase("DoS Attack")) {
            alert.setDangerLevel("Élevé");
        } else if (alert.getType().equalsIgnoreCase("Port Scan")) {
            alert.setDangerLevel("Modéré");
        } else {
            alert.setDangerLevel("Faible");
        }

        alertList.add(alert);

        Platform.runLater(() -> {
            // Mettez à jour la table des alertes ici
        });
    }
}
