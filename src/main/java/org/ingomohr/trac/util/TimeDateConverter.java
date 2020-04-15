package org.ingomohr.trac.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Converts time-representational Strings to Dates.
 */
public class TimeDateConverter {

    /**
     * Converts the given time (HH:mm) to {@link Date}.
     * 
     * @param time the time value. Cannot be <code>null</code>.
     * @return date. Never <code>null</code>.
     * @throws RuntimeException if time cannot be parsed.
     */
    public Date toDate(String time) {
        Objects.requireNonNull(time);

        String pattern = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot read time stamp.", e);
        }

    }

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

}
