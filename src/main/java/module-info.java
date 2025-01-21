module org.app.traficvi {
    requires org.pcap4j.core;
    requires java.desktop;
    requires com.opencsv;
    requires org.apache.pdfbox;

    requires javafx.controls;
    requires javafx.fxml;

    exports org.app.traficvi;
    exports org.app.traficvi.controllers;


    opens org.app.traficvi.controllers to javafx.fxml;
}