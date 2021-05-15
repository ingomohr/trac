package org.ingomohr.trac.util;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * Converts time-representational data.
 */
public class TimeConverter {

    /**
     * Converts the given minutes to a HH:mm format.
     * 
     * @param minutes the minutes.
     * @return the HH:mm representation.
     */
    public String toHHmm(int minutes) {
        int hours = minutes / 60;
        int min = minutes % 60;
        final String format = String.format("%d:%02d", hours, min);
        return format;
    }

    /**
     * Converts the given time values to string format of HH:mm.
     * 
     * @param time the time value.
     * @return string representation. <code>null</code> if time was
     *         <code>null</code>..
     */
    public String toString(TemporalAccessor time) {
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

    public int getMinutes(TemporalAccessor time) {
        return time.get(ChronoField.MINUTE_OF_HOUR);
    }

    public int getHours(TemporalAccessor time) {
        int hh = time.get(ChronoField.HOUR_OF_DAY);
        return hh;
    }

}
