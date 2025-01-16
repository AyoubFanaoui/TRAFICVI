package org.app.traficvi.model;

import org.pcap4j.core.PcapNetworkInterface;


import java.util.List;

public interface TrafficCapture {
    List<NetworkPacket> captureTrafic(PcapNetworkInterface networkInterface);
    List<PcapNetworkInterface>  NetworkInterface();


}
