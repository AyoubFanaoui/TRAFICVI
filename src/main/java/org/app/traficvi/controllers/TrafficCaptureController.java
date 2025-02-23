package org.app.traficvi.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.app.traficvi.model.NetworkPacket;
import org.app.traficvi.model.TrafficListener;
import org.pcap4j.core.PcapNetworkInterface;

public class TrafficCaptureController {

    private TrafficListener trafficListener;
    private Thread captureThread;

    public TrafficCaptureController(TrafficListener trafficListener) {
        this.trafficListener = trafficListener;
    }

    public void startCapture(PcapNetworkInterface selectedNetworkInterface) {
        if (selectedNetworkInterface != null) {
            captureThread = new Thread(() -> trafficListener.captureTrafic(selectedNetworkInterface));
            captureThread.start();
        }
    }

    public void stopCapture() {
        if (captureThread != null && captureThread.isAlive()) {
            captureThread.interrupt();
        }
    }

    public void updateTableView(NetworkPacket packet, ObservableList<NetworkPacket> capturedPackets) {
        Platform.runLater(() -> capturedPackets.add(packet));
    }
}
