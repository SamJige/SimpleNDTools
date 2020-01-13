package com.jige.service;

import org.slf4j.LoggerFactory;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.io.InputStream;
import java.util.function.BiConsumer;

public class SimpleRead implements SerialPortEventListener {
    CommPortIdentifier portId;
    InputStream inputStream;
    SerialPort serialPort;
    BiConsumer<Integer, byte[]> readData;

    public SimpleRead(CommPortIdentifier portId, BiConsumer<Integer, byte[]> readData) {
        try {
            this.portId = portId;
            this.readData = readData;
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
            inputStream = serialPort.getInputStream();
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.setSerialPortParams(57600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            LoggerFactory.getLogger(getClass()).info("init ok ");
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("error ->", e);
        }
    }

    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[256];
                int numBytes = 0;
                try {
                    while (inputStream.available() > 0) {
                        numBytes = inputStream.read(readBuffer);
                    }
                    readData.accept(numBytes, readBuffer);
                } catch (Exception e) {
                    LoggerFactory.getLogger(getClass()).error("error ->", e);
                }
                break;
        }
    }
}