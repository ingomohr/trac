package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
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
	 * Returns the duration of the item.
	 * 
	 * @param item the item to inspect. Cannot be <code>null</code>.
	 * @return duration. <code>null</code> if not both of start time and end time in
	 *         the item are specified.
	 */
	public Duration getDuration(TracItem item) {
		requireNonNull(item);

		TemporalAccessor startTime = item.startTime();
		TemporalAccessor endTime = item.endTime();

		if (startTime != null && endTime != null) {
			Duration duration = new DurationCalculator().calculateDuration(startTime, endTime);
			return duration;
		}

		return null;
	}

	/**
	 * Returns the duration of the given item as string.
	 * 
	 * @param item the item. Cannot be <code>null</code>.
	 * @return duration as string representation. <code>null</code> if item has no
	 *         duration.
	 */
	public String getDurationAsString(TracItem item) {
		requireNonNull(item);

		Duration duration = getDuration(item);
		if (duration != null) {
			return new DurationToStringConverter().toString(duration);
		}
		return null;
	}

}
