package org.ingomohr.trac.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

import org.ingomohr.trac.model.IWorklogItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracWorklogItemInspector {

    private TracWorklogItemInspector objUT;

    @BeforeEach
    void prep() {
        objUT = new TracWorklogItemInspector();
    }

    @Test
    void getStartTimeAsString_ForItemWithoutStartTime_ReturnsNull() {
        assertNull(objUT.getStartTimeAsString(mkItem(null, "13:10")));
    }

    @Test
    void getStartTimeAsString_ForItemWithStartTime_ReturnsStartTimeAsString() {
        assertEquals("05:23", objUT.getStartTimeAsString(mkItem("05:23", "13:10")));
    }

    @Test
    void getEndTimeAsString_ForItemWithEndTime_ReturnsEndTimeAsString() {
        assertEquals("13:10", objUT.getEndTimeAsString(mkItem("05:23", "13:10")));
    }

    @Test
    void getEndTimeAsString_ForItemWithoutEndTime_ReturnsNull() {
        assertNull(objUT.getEndTimeAsString(mkItem("13:10", null)));
    }

    @Test
    void getTimeSpentInMinutes() {
        assertAll(() -> {

            assertEquals(-1, objUT.getTimeSpentInMinutes(mkItem(null, null)));
            assertEquals(-1, objUT.getTimeSpentInMinutes(mkItem(null, "05:00")));
            assertEquals(-1, objUT.getTimeSpentInMinutes(mkItem("04:00", null)));
            assertEquals(84, objUT.getTimeSpentInMinutes(mkItem("04:00", "05:24")));
        });

    }

    private IWorklogItem mkItem(String startTime, String endTime) {
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

}
