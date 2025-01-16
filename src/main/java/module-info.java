module org.app.traficvi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.pcap4j.core;
    requires java.desktop;


    opens org.app.traficvi to javafx.fxml;
    exports org.app.traficvi;
}