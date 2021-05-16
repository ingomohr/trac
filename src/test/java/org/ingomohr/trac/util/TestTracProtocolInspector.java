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

import org.ingomohr.trac.model.ITracItem;
import org.ingomohr.trac.model.ITracProtocol;
import org.ingomohr.trac.model.IWorklogItem;
import org.ingomohr.trac.model.impl.TracProtocol;
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
            assertNull(objUT.getStartTime(mkProtocol(mkItem(), mkWorklogItem(null, "05:00"))));
            assertNull(objUT
                    .getStartTime(mkProtocol(mkItem(), mkWorklogItem(null, "05:00"), mkWorklogItem("04:00", "05:00"))));
            assertEquals("04:10", toHHmm(objUT.getStartTime(mkProtocol(mkItem(), mkWorklogItem("04:10", "05:00")))));
            assertEquals("04:10", toHHmm(objUT.getStartTime(mkProtocol("04:10", "05:00"))));
        });
    }

    @Test
    void getEndTime() {
        assertAll(() -> {
            assertNull(objUT.getEndTime(mkProtocol()));
            assertNull(objUT.getEndTime(mkProtocol("05:00", null)));
            assertNull(objUT.getEndTime(mkProtocol(mkItem(), mkWorklogItem("05:00", null), mkItem())));
            assertNull(objUT.getEndTime(
                    mkProtocol(mkItem(), mkWorklogItem("05:00", "06:00"), mkWorklogItem(null, null), mkItem())));
            assertEquals("05:00", toHHmm(objUT.getEndTime(mkProtocol(mkWorklogItem("04:10", "05:00"), mkItem()))));
            assertEquals("05:00", toHHmm(objUT.getEndTime(mkProtocol("04:10", "05:00"))));
        });
    }

    @Test
    void getTimeSpanInMinutes() {
        assertAll(() -> {
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol()));
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol(mkItem())));
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol(mkWorklogItem(null, "01:00"))));
            assertEquals(-1, objUT.getTimeSpanInMinutes(mkProtocol(mkWorklogItem("01:00", null))));
            assertEquals(83, objUT.getTimeSpanInMinutes(mkProtocol(mkItem(), mkWorklogItem("05:00", "06:23"))));
            assertEquals(83,
                    objUT.getTimeSpanInMinutes(mkProtocol(mkItem(), mkWorklogItem("05:00", "06:23"), mkItem())));
            assertEquals(120, objUT.getTimeSpanInMinutes(mkProtocol(mkWorklogItem("23:00", "01:00"))));
        });

    }

    private ITracProtocol mkProtocol(ITracItem... items) {
        ITracProtocol protocol = new TracProtocol();

        protocol.getItems().addAll(Arrays.asList(items));

        return protocol;
    }

    private ITracProtocol mkProtocol(String startTime, String endTime) {
        ITracProtocol protocol = new TracProtocol();

        protocol.getItems().add(mkWorklogItem(startTime, null));
        protocol.getItems().add(mkWorklogItem(null, endTime));

        return protocol;
    }

    private ITracItem mkItem() {
        ITracItem item = mock(ITracItem.class);
        return item;
    }

    private IWorklogItem mkWorklogItem(String startTime, String endTime) {
        IWorklogItem item = mock(IWorklogItem.class);
        when(item.getStartTime()).thenReturn(mkTime(startTime));
        when(item.getEndTime()).thenReturn(mkTime(endTime));
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
