package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * Converts time-representational data.
 */
public class TimeConverter {

    /**
     * Converts the given minutes to a HH:mm format.
     * <p>
     * If the number of hours exceeds 99, this will use for than 2 chars for the
     * hours.
     * </p>
     * 
     * @param minutes the minutes.
     * @return the HH:mm representation.
     */
    public String toHHmm(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative: " + minutes);
        }
        int hours = minutes / 60;
        int min = minutes % 60;
        final String format = String.format("%02d:%02d", hours, min);
        return format;
    }

    /**
     * Converts the given time value to a HH:mm format.
     * 
     * @param time the time value.
     * @return string representation. Cannot be <code>null</code>.
     */
    public String toHHmm(TemporalAccessor time) {
        requireNonNull(time);

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

    public int getMinutes(TemporalAccessor time) {
        return time.get(ChronoField.MINUTE_OF_HOUR);
    }

    public int getHours(TemporalAccessor time) {
        int hh = time.get(ChronoField.HOUR_OF_DAY);
        return hh;
    }

}
