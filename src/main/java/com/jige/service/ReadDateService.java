package com.jige.service;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import purejavacomm.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Service
public class ReadDateService {
    int SYNC = 0xAA;
    int EXCODE = 0x55;

    public void waitForData() {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals("COM4")) {
                    //                if (portId.getName().equals("/dev/term/a")) {
                    SimpleRead reader = new SimpleRead(portId, (readSize, readBuffer) -> {
                        parse2(readBuffer, readSize);
//                        String str = new String(readBuffer);
//                        LoggerFactory.getLogger(getClass()).info("readSize:{}  str -> {}", readSize, str);
                    });
                }
            }
        }
    }

    void parse2(byte[] packet, int readSize) {
        if (packet.length < 4) {
            return;
        }
        int pLength;
        int checkSum;
        for (int i = 2; i < readSize; i++) {
            byte sync0 = packet[i - 2];
            byte sync1 = packet[i - 1];
            byte thisByte = packet[i];
            if (sync0 != sync1 || Byte.toUnsignedInt(sync0) != 0xaa) {
                continue;
            }
            //前两个是 0xaa 第三个是长度
            pLength = Byte.toUnsignedInt(thisByte);

            checkSum = Byte.toUnsignedInt(packet[i + pLength + 1]);
//            LoggerFactory.getLogger(getClass()).info("length:{} checksum :{}", pLength, checkSum);
            if (pLength == 0) {
                continue;
            }
            List<Byte> dataPayload = new ArrayList<>(pLength);
            for (int j = i + 1; j <= i + pLength; j++) {
                dataPayload.add(packet[j]);
            }
            if (!isGoodCheckSum(dataPayload, checkSum)) {
                // read data
                continue;
            }
            LoggerFactory.getLogger(getClass()).info("data size -> {}", dataPayload.size());
            i += pLength;
        }
    }

    /**
     * The Payload's Checksum is defined as:
     * 1. summing all the bytes of the Packet's Data Payload
     * 2. taking the lowest 8 bits of the sum
     * 3. performing the bit inverse (one's compliment inverse) on those lowest 8 bits
     */
    public boolean isGoodCheckSum(List<Byte> payload, int checkSum) {
        int sum = payload.stream().mapToInt(Byte::toUnsignedInt).sum();
        int a = (~(sum & 0xff)) & 0xff;

        if (a != checkSum) {
            LoggerFactory.getLogger(getClass()).info("bad checksum sum:{} checkSum:{}", sum, checkSum);
            return false;
        }
        return true;
    }

    @Deprecated
    int parsePayload(byte[] payload, int pLength) {
        int bytesParsed = 0;
        int code;
        int length;
        int extendedCodeLevel;
        int i;
        /* Loop until all bytes are parsed from the payload[] array... */
        while (bytesParsed < pLength) {
            /* Parse the extendedCodeLevel, code, and length */
            extendedCodeLevel = 0;
            while (payload[bytesParsed] == EXCODE) {
                extendedCodeLevel++;
                bytesParsed++;
            }
            code = payload[bytesParsed++];
            if ((code & 0x80) > 0) {
                length = payload[bytesParsed++];
            } else {
                length = 1;
            }
            /* TODO: Based on the extendedCodeLevel, code, length, * and the [CODE] Definitions Table, handle the next
             * "length" bytes of data from the payload as
             * appropriate for your application.
             */
            LoggerFactory.getLogger(getClass()).info(String.format(
                    "EXCODE level: %d CODE: 0x%02x length: %d ", extendedCodeLevel, code, length
            ));
            List<String> bitsStr = new ArrayList<>();
            for (i = 0; i < length; i++) {
                bitsStr.add(String.format(" %02x", payload[bytesParsed + i] & 0xFF));
            }
            LoggerFactory.getLogger(getClass()).info("Data value(s): {}", bitsStr);
            /* Increment the bytesParsed by the length of the Data Value */
            bytesParsed += length;
        }
        return 0;
    }
}
