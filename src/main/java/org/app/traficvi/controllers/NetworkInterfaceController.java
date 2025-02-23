package org.app.traficvi.controllers;

import org.pcap4j.core.PcapNetworkInterface;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.*;

public class NetworkInterfaceController {

    public void loadNetworkInterfaces(ObservableList<String> items, List<PcapNetworkInterface> availableInterfaces) {
        for (PcapNetworkInterface networkInterface : availableInterfaces) {
            items.add(networkInterface.getName());
        }
    }

    public PcapNetworkInterface getSelectedNetworkInterface(ComboBox<String> networkInterfacesComboBox, List<PcapNetworkInterface> availableInterfaces) {
        String selectedName = networkInterfacesComboBox.getSelectionModel().getSelectedItem();
        return availableInterfaces.stream()
                .filter(ni -> ni.getName().equals(selectedName))
                .findFirst()
                .orElse(null);
    }
}
