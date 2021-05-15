package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.temporal.TemporalAccessor;

import org.ingomohr.trac.model.IWorklogItem;

/**
 * Computes information from {@link IWorklogItem}s.
 */
public class TracWorklogItemInspector {

    /**
     * Returns the start time of the given {@link IWorklogItem} as string.
     * 
     * @param item the item to inspect. Cannot be <code>null</code>.
     * @return start time as string. <code>null</code> if item has no start time.
     */
    public String getStartTimeAsString(IWorklogItem item) {
        requireNonNull(item);

        TemporalAccessor startTime = item.getStartTime();
        return new TimeConverter().toHHmm(startTime);
    }

    /**
     * Returns the end time of the given {@link IWorklogItem} as string.
     * 
     * @param item the item to inspect. Cannot be <code>null</code>.
     * @return end time as string. <code>null</code> if item has no end time.
     */
    public String getEndTimeAsString(IWorklogItem item) {
        requireNonNull(item);

        TemporalAccessor startTime = item.getEndTime();
        return new TimeConverter().toHHmm(startTime);
    }

    /**
     * Returns the number of minutes spent on the given item.
     * 
     * @param item the item to inspect. Cannot be <code>null</code>.
     * @return minutes spent. <code>-1<code> if not both start- and end time are
     *         set.
     */
    public int getTimeSpentInMinutes(IWorklogItem item) {
        requireNonNull(item);

        TemporalAccessor startTime = item.getStartTime();
        TemporalAccessor endTime = item.getEndTime();
        return new TimeDiffCalculator().getDiffInMinutes(startTime, endTime);
    }

}
