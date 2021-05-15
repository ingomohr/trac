package org.ingomohr.trac.util;

import java.time.temporal.TemporalAccessor;

/**
 * Calculates the time diff.
 */
public class TimeDiffCalculator {

    /**
     * Returns the diff in minutes between the given start time and the given end
     * time.
     * 
     * @param startTime start time.
     * @param endTime   end time.
     * @return time diff in minutes. <code>-1</code> if not both of the given times
     *         are specified.
     */
    public int getDiffInMinutes(TemporalAccessor startTime, TemporalAccessor endTime) {
        if (startTime != null && endTime != null) {
            final TimeConverter tc = new TimeConverter();

            int hh1 = tc.getHours(startTime);
            int mm1 = tc.getMinutes(startTime);

            int hh2 = tc.getHours(endTime);
            int mm2 = tc.getMinutes(endTime);

            if (hh2 < hh1) {
                hh2 += 24;
            }

            return (hh2 - hh1) * 60 + (mm2 - mm1);
        }

        return -1;
    }

}
