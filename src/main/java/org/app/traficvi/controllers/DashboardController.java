package org.app.traficvi.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.app.traficvi.model.*;
import org.pcap4j.core.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashboardController {

    private TrafficListener trafficListener;
    private List<PcapNetworkInterface> availableInterfaces;
    private PcapNetworkInterface selectedNetworkInterface;
    private Thread captureThread;

    @FXML
    private Button stopCaptureButton;
    @FXML
    private Button startCaptureButton;

    @FXML
    private Button pdfButton;
    @FXML
    private Button csvButton;

    @FXML
    private TableView<NetworkPacket> connectionsTable;
    //table view
    @FXML
    private TableColumn<NetworkPacket, String> sourceIpColumn;
    @FXML
    private TableColumn<NetworkPacket, String> destIpColumn;
    @FXML
    private TableColumn<NetworkPacket, String> ProtocoleColumn;
    @FXML
    private TableColumn<NetworkPacket, String> portColumn;
    @FXML
    private TableColumn<NetworkPacket, String> PacketContent;
    @FXML
    private TableColumn<NetworkPacket, String> tailleColumn;
    @FXML
    private TableColumn<NetworkPacket, String> timestampColumn;
    //######
    //alert
    @FXML
    private TableView<IntrusionAlert> alertTable;

    @FXML
    private TableColumn<IntrusionAlert, String> typeColumn;
    @FXML
    private TableColumn<IntrusionAlert, String> InstrusionColumn;
    @FXML
    private TableColumn<IntrusionAlert, String> timestampColumnAlert;
    @FXML
    private TableColumn<IntrusionAlert, String> dangerLevelColumn;

    private final ObservableList<IntrusionAlert> alertList = FXCollections.observableArrayList();
    //#####

    @FXML
    private ComboBox<String> networkInterfacesComboBox;

    @FXML
    private Label totalConnectionsLabel;
    @FXML
    private Label totalDataTransferredLabel;

    private final ObservableList<NetworkPacket> capturedPackets = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialiser TrafficListener avec les callbacks appropriés
        trafficListener = new TrafficListener(this::updateTableView, this::updateStatsUI, this::updateAlertTable);

        // Configurer les colonnes de la table
        sourceIpColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSourceIP()));
        destIpColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDestinationIP()));
        ProtocoleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProtocole()));
        portColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPort())));
        PacketContent.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPacketContent()));
        tailleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSize())));
        timestampColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp()));

        // Associer la liste observable à la table
        connectionsTable.setItems(capturedPackets);

        // Charger les interfaces réseau
        availableInterfaces = trafficListener.NetworkInterface();
        for (PcapNetworkInterface networkInterface : availableInterfaces) {
            networkInterfacesComboBox.getItems().add(networkInterface.getName());
        }

        // Sélection d'une interface
        networkInterfacesComboBox.setOnAction(event -> {
            String selectedName = networkInterfacesComboBox.getSelectionModel().getSelectedItem();
            selectedNetworkInterface = availableInterfaces.stream()
                    .filter(ni -> ni.getName().equals(selectedName))
                    .findFirst()
                    .orElse(null);
        });

        // Initialiser la table des alertes
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        InstrusionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIntrusion()));
        timestampColumnAlert.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestampString()));
        dangerLevelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDangerLevel()));


        // Associer la liste observable aux alertes
        alertTable.setItems(alertList);

        stopCaptureButton.setDisable(true); // Désactiver le bouton Stop au début
    }

    public void startCapture(ActionEvent actionEvent) {
        if (selectedNetworkInterface != null) {
            captureThread = new Thread(() -> trafficListener.captureTrafic(selectedNetworkInterface));
            captureThread.start();

            startCaptureButton.setDisable(true);
            stopCaptureButton.setDisable(false);
        } else {
            System.out.println("Aucune interface réseau sélectionnée !");
        }
    }

    public void stopCapture(ActionEvent actionEvent) {
        if (captureThread != null && captureThread.isAlive()) {
            captureThread.interrupt();
            startCaptureButton.setDisable(false);
            stopCaptureButton.setDisable(true);
        }
    }

    private void updateTableView(NetworkPacket packet) {
        Platform.runLater(() -> capturedPackets.add(packet));
    }

    public void updateAlertTable(IntrusionAlert alert) {
        // Ajout de l'alerte à la liste observable


        if (alert.getType().equalsIgnoreCase("DoS Attack")) {
            alert.setDangerLevel("Élevé");
        } else if (alert.getType().equalsIgnoreCase("Port Scan")) {
            alert.setDangerLevel("Modéré");
        } else {
            alert.setDangerLevel("Faible");
        }

        alertList.add(alert);

        // Si vous souhaitez rafraîchir le tableau
        Platform.runLater(() -> {
            alertTable.setItems(alertList);
        });
    }

    private void updateStatsUI() {
        Platform.runLater(() -> {
            totalConnectionsLabel.setText("Total des Connexions : " + NetworkStatistics.getTotalConnections());
            totalDataTransferredLabel.setText("Données Transférées : " + NetworkStatistics.getTotalDataTransferred() + " octets");
        });
    }

    @FXML
    public void generatePDFReport(ActionEvent actionEvent) {
        ReportGenerator reportGenerator = new ReportGenerator(alertList);
        if (reportGenerator.generateReport("PDF")) {
            showAlert("Succès", "Rapport PDF généré avec succès !");
        } else {
            showAlert("Erreur", "Échec de la génération du rapport PDF.");
        }
    }

    @FXML
    public void generateCSVReport(ActionEvent actionEvent) {
        ReportGenerator reportGenerator = new ReportGenerator(alertList);
        if (reportGenerator.generateReport("CSV")) {
            showAlert("Succès", "Rapport CSV généré avec succès !");
        } else {
            showAlert("Erreur", "Échec de la génération du rapport CSV.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
