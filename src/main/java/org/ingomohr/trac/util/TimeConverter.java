package org.ingomohr.trac.util;

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

}
