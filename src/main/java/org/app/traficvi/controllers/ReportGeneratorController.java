package org.app.traficvi.controllers;

import javafx.collections.ObservableList;
import org.app.traficvi.model.IntrusionAlert;
import org.app.traficvi.model.ReportGenerator;
import javafx.scene.control.Alert;

public class ReportGeneratorController {

    public boolean generateReport(String reportType, ObservableList<IntrusionAlert> alertList) {
        ReportGenerator reportGenerator = new ReportGenerator(alertList);
        return reportGenerator.generateReport(reportType);
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
