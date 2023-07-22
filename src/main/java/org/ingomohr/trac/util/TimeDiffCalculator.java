package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.temporal.TemporalAccessor;

/**
 * Calculates the time diff.
 */
public class TimeDiffCalculator {

	/**
	 * Returns the diff in minutes between the given start time and the given end
	 * time.
	 * <p>
	 * If the end time is smaller than/"before" the start time, this considers the
	 * end time to be located on the next day - e.g. diff("23:30", "00:30") is
	 * 60min.
	 * </p>
	 * 
	 * @param startTime start time. Cannot be <code>null</code>.
	 * @param endTime   end time. Cannot be <code>null</code>.
	 * @return time diff in minutes.
	 */
	public int getDiffInMinutes(TemporalAccessor startTime, TemporalAccessor endTime) {
		requireNonNull(startTime);
		requireNonNull(endTime);

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

}
