package com.gamusdev.lowlatency.performance.tests.aeronvega.utils;

import java.util.stream.LongStream;

/**
 * Calculates the checksum
 */
public class Checksum {

    public static long getChecksum(int size) {
        return LongStream.rangeClosed( 1, size ).sum();
    }

}
