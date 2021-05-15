package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.temporal.ChronoField;
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
        return toString(startTime);
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
        return toString(startTime);
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
        if (startTime != null && endTime != null) {
            int hh1 = getHours(startTime);
            int mm1 = getMinutes(startTime);

            int hh2 = getHours(endTime);
            int mm2 = getMinutes(endTime);

            if (hh2 < hh1) {
                hh2 += 24;
            }

            return (hh2 - hh1) * 60 + (mm2 - mm1);
        }

        return -1;
    }

    protected String toString(TemporalAccessor time) {
        if (time != null) {
            int hh = getHours(time);
            int mm = getMinutes(time);

            StringBuilder builder = new StringBuilder();

            if (hh < 10) {
                builder.append("0");
            }
            builder.append(hh).append(":");
            if (mm < 10) {
                builder.append("0");
            }
            builder.append(mm);

            return builder.toString();
        }
        return null;
    }

    private int getMinutes(TemporalAccessor time) {
        return time.get(ChronoField.MINUTE_OF_HOUR);
    }

    private int getHours(TemporalAccessor time) {
        int hh = time.get(ChronoField.HOUR_OF_DAY);
        return hh;
    }

}
