package org.ingomohr.trac.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTimeConverter {

    private TimeConverter objUT;

    @BeforeEach
    void prep() {
        objUT = new TimeConverter();
    }

    @Test
    void getHours() {
        assertAll(() -> {
            assertEquals(0, objUT.getHours(mkTempoaralAccessor("00:13")));
            assertEquals(14, objUT.getHours(mkTempoaralAccessor("14:13")));
            assertEquals(23, objUT.getHours(mkTempoaralAccessor("23:13")));
        });
    }

    @Test
    void getMinutes() {
        assertAll(() -> {
            assertEquals(0, objUT.getMinutes(mkTempoaralAccessor("01:00")));
            assertEquals(35, objUT.getMinutes(mkTempoaralAccessor("14:35")));
            assertEquals(59, objUT.getMinutes(mkTempoaralAccessor("23:59")));
        });
    }

    @Test
    void toHHmm_ForTemporalAccessor() {
        assertAll(() -> {
            assertEquals("00:00", objUT.toHHmm(mkTempoaralAccessor("00:00")));
            assertEquals("05:34", objUT.toHHmm(mkTempoaralAccessor("05:34")));
            assertEquals("14:35", objUT.toHHmm(mkTempoaralAccessor("14:35")));
            assertEquals("23:59", objUT.toHHmm(mkTempoaralAccessor("23:59")));
        });
    }

    @Test
    void toHHmm_ForMinutes() {
        assertAll(() -> {
            assertEquals("00:00", objUT.toHHmm(0));
            assertEquals("00:59", objUT.toHHmm(59));
            assertEquals("01:00", objUT.toHHmm(60));
            assertEquals("20:02", objUT.toHHmm(1_202));
            assertEquals("200:20", objUT.toHHmm(12_020));
        });
    }

    @Test
    void toHHmm_ForNegativeMinutes_IllegalArgExceptionWasThrown() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> objUT.toHHmm(-1));
        assertThat(ex.getMessage(), CoreMatchers.containsString("cannot be negative: -1"));
    }

    private TemporalAccessor mkTempoaralAccessor(String HHmm) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
        return formatter.parse(HHmm);
    }

}
