package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.temporal.TemporalAccessor;

import org.ingomohr.trac.model.TracItem;

/**
 * Provides information on {@link TracItem}s.
 */
public class TracItemInspector {

    /**
     * Returns the start time of the given {@link TracItem} as string.
     * 
     * @param item the item to inspect. Cannot be <code>null</code>.
     * @return start time as string. <code>null</code> if item has no start time.
     */
    public String getStartTimeAsString(TracItem item) {
        requireNonNull(item);

        TemporalAccessor startTime = item.startTime();
        if (startTime != null) {

            return new TimeConverter().toHHmm(startTime);
        }
        return null;
    }

    /**
     * Returns the end time of the given {@link TracItem} as string.
     * 
     * @param item the item to inspect. Cannot be <code>null</code>.
     * @return end time as string. <code>null</code> if item has no end time.
     */
    public String getEndTimeAsString(TracItem item) {
        requireNonNull(item);

        TemporalAccessor startTime = item.endTime();
        if (startTime != null) {
            return new TimeConverter().toHHmm(startTime);
        }
        return null;
    }

    /**
     * Returns the number of minutes spent on the given {@link TracItem}.
     * 
     * @param item the item to inspect. Cannot be <code>null</code>.
     * @return minutes spent. <code>-1<code> if not both start- and end time are
     *         set.
     */
    public int getTimeSpentInMinutes(TracItem item) {
        requireNonNull(item);

        TemporalAccessor startTime = item.startTime();
        TemporalAccessor endTime = item.endTime();

        if (startTime != null && endTime != null) {
            return new TimeDiffCalculator().getDiffInMinutes(startTime, endTime);
        }

        return -1;

    }

}
