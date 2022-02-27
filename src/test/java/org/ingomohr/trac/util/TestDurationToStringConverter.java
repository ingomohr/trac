package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DurationToStringConverter}.
 */
public class TestDurationToStringConverter {

    private DurationToStringConverter objUT;

    @BeforeEach
    void prep() {
        objUT = new DurationToStringConverter();
    }

    @Test
    void test() {
        assertAll(() -> {
            assertEquals(" 0m", objUT.toString(Duration.ofMinutes(0)));
            assertEquals("56m", objUT.toString(Duration.ofMinutes(56)));
            assertEquals(" 1h  0m", objUT.toString(Duration.ofMinutes(60)));
            assertEquals(" 1h 22m", objUT.toString(Duration.ofMinutes(82)));
            assertEquals("1d  0h  0m", objUT.toString(Duration.ofMinutes(1440)));
            assertEquals("1d  1h  0m", objUT.toString(Duration.ofMinutes(1500)));
            assertEquals("1d  1h 59m", objUT.toString(Duration.ofMinutes(1559)));
            assertEquals("1d 11h  9m", objUT.toString(Duration.ofMinutes(2109)));
            assertEquals("1d 11h 59m", objUT.toString(Duration.ofMinutes(2159)));
            assertEquals("1.000d  0h  0m", objUT.toString(Duration.ofMinutes(1_000 * 1440)));
            assertEquals("1.000.000d  0h  0m", objUT.toString(Duration.ofMinutes(1_000_000 * 1440)));
        });
    }

}
