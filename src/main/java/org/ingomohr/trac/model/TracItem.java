package org.ingomohr.trac.model;

import java.time.temporal.TemporalAccessor;

/**
 * An item in a protocol. An item can have a start time, end time and a text.
 * <p>
 * Note the items are immutable. You cannot modify them.
 * </p>
 */
public record TracItem(

        TemporalAccessor startTime,

        TemporalAccessor endTime,

        String text

) {

    /**
     * Returns a new {@link TracItem} with the properties from this item - and the
     * given endTime instead of this item's endTime.
     * 
     * @param endTime the end time to use.
     * @return new {@link TracItem} with properties from this one and the given end
     *         time. Never <code>null</code>.
     */
    public TracItem withEndTime(TemporalAccessor endTime) {
        return new TracItem(startTime(), endTime, text());
    }

}
