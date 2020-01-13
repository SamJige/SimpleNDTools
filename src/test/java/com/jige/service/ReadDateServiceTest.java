package com.jige.service;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Arrays;

class ReadDateServiceTest {

    @Test
    void isGoodCheckSum() {
        Assert.isTrue(
                new ReadDateService().isGoodCheckSum(
                        Arrays.asList(
                                (byte) 2, (byte) 0xfe
                        ),
                        0x00
                ), "错误"
        );
    }

    @Test
    void tst2() {
//        LoggerFactory.getLogger(getClass()).info("long -> {}", (Byte.MIN_VALUE & Byte.MAX_VALUE) == 0);
        LoggerFactory.getLogger(getClass()).info("long -> {}", Byte.toUnsignedInt(Byte.MIN_VALUE));
    }

    @Test
    void tst3() {
        LoggerFactory.getLogger(getClass()).info("long -> {}", Integer.toBinaryString((~(0x1 & 0xff)) & 0xff));
    }
}