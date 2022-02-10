package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracProtocolInspector {

    private TracProtocolInspector objUT;

    @BeforeEach
    void prep() {
        objUT = new TracProtocolInspector();
    }

    @Test
    void getStartTime() {
        assertAll(() -> {
            assertNull(objUT.getStartTime(mkProtocol()));
            assertNull(objUT.getStartTime(mkProtocol(null, "05:00")));
            assertNull(objUT.getStartTime(mkProtocol(mkItem(null, null), mkItem("05:00", "06:00"))));
            assertEquals("04:10", toHHmm(objUT.getStartTime(mkProtocol("04:10", "05:00"))));
            assertEquals("01:00", toHHmm(objUT.getStartTime(mkProtocol(mkItem("01:00", null), mkItem("02:22", null)))));
        });
    }

    @Test
    void getEndTime() {
        assertAll(() -> {
            assertNull(objUT.getEndTime(mkProtocol()));
            assertNull(objUT.getEndTime(mkProtocol("05:00", null)));
            assertNull(objUT.getEndTime(mkProtocol(mkItem("05:00", "06:00"), mkItem(null, null))));
            assertEquals("05:00", toHHmm(objUT.getEndTime(mkProtocol("04:10", "05:00"))));
            assertEquals("02:22", toHHmm(objUT.getEndTime(mkProtocol(mkItem(null, "01:00"), mkItem(null, "02:22")))));
        });
    }

    @Test
    void getTimeSpanInMinutes() {
        assertAll(() -> {
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol()));
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol(mkItem(null, null))));
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol(mkItem(null, "01:00"))));
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol(mkItem("01:00", null))));
            assertEquals(83, objUT.getTimeSpanInMinutes(mkProtocol(mkItem("05:00", "06:23"))));
            assertEquals(120, objUT.getTimeSpanInMinutes(mkProtocol(mkItem("23:00", "01:00"))));
        });

    }

    private TracProtocol mkProtocol() {
        return new TracProtocol();
    }

    private TracProtocol mkProtocol(TracItem... items) {
        TracProtocol protocol = new TracProtocol();

        protocol.items().addAll(Arrays.asList(items));

        return protocol;
    }

    private TracProtocol mkProtocol(String startTime, String endTime) {
        TracProtocol protocol = new TracProtocol();

        protocol.items().add(mkItem(startTime, null));
        protocol.items().add(mkItem(null, endTime));

        return protocol;
    }

    private TracItem mkItem(String startTime, String endTime) {
        TracItem item = mock(TracItem.class);
        when(item.startTime()).thenReturn(mkTime(startTime));
        when(item.endTime()).thenReturn(mkTime(endTime));
        return item;
    }

    private TemporalAccessor mkTime(String HHmm) {
        if (HHmm != null) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
            return formatter.parse(HHmm);
        }
        return null;
    }

    private String toHHmm(TemporalAccessor time) {
        return new TimeConverter().toHHmm(time);
    }

}
