package com.jige.service;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import purejavacomm.CommPortIdentifier;

import java.util.Enumeration;

@Service
public class ReadDateService {
    public void waitForData() {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals("COM1")) {
                    //                if (portId.getName().equals("/dev/term/a")) {
                    SimpleRead reader = new SimpleRead(portId, readBuffer -> {
                        String str = new String(readBuffer);
                        LoggerFactory.getLogger(getClass()).info("str -> {}", str);
                    });
                }
            }
        }
    }
}
