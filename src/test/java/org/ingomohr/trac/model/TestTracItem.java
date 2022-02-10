package org.ingomohr.trac.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TracItem}.
 */
public class TestTracItem {

    @Test
    void withEndTime() {

        TracItem item1 = new TracItem(mkTime(), mkTime(), "text");

        TemporalAccessor newTime = mkTime();

        assertNotSame(newTime, item1.startTime());
        assertNotSame(newTime, item1.endTime());

        TracItem newItem = item1.withEndTime(newTime);
        assertNotSame(newItem, item1);
        assertSame(item1.startTime(), newItem.startTime());
        assertSame(newTime, newItem.endTime());
        assertEquals("text", newItem.text());
    }

    private TemporalAccessor mkTime() {
        return mock(TemporalAccessor.class);
    }

}
