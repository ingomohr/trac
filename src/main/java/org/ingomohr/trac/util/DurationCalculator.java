package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;

/**
 * Calculates the {@link Duration} from a given start time to a given end time.
 */
public class DurationCalculator {

	/**
	 * Returns the duration from a given start time to a given end time.
	 * <p>
	 * The returned duration is always positive. If the end time is smaller than the
	 * start time, this assumes that the end time is located on the next day.
	 * </p>
	 * 
	 * @param startTime the start time. Cannot be <code>null</code>.
	 * @param endTime   the end time. Cannot be <code>null</code>.
	 * @return duration. Never <code>null</code>.
	 */
	public Duration calculateDuration(TemporalAccessor startTime, TemporalAccessor endTime) {

		requireNonNull(startTime);
		requireNonNull(endTime);

		final TimeConverter converter = new TimeConverter();

		int hours1 = converter.getHours(startTime);
		int mins1 = converter.getMinutes(startTime);
		int hours2 = converter.getHours(endTime);
		int mins2 = converter.getMinutes(endTime);

		if (mins2 < mins1) {
			mins2 += 60;
			hours2 -= 1;

		}
		if (hours2 < hours1) {
			hours2 += 24;
		}

		Duration duration = Duration.ofHours(hours2 - hours1).plusMinutes(mins2 - mins1);
		return duration;
	}

}
