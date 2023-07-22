package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;

/**
 * Provides information on an {@link TracProtocol}.
 */
public class TracProtocolInspector {

	/**
	 * Returns the start time of the protocol.
	 * <p>
	 * The start time is the start time of the first {@link TracItem} in the
	 * protocol.
	 * </p>
	 * 
	 * @param protocol the protocol. Cannot be <code>null</code>.
	 * @return start time. <code>null</code> if none found.
	 */
	public TemporalAccessor getStartTime(TracProtocol protocol) {
		Objects.requireNonNull(protocol);

		TracItem firstItem = protocol.items().stream().findFirst().orElse(null);
		if (firstItem != null) {
			return firstItem.startTime();
		}
		return null;
	}

	/**
	 * Returns the end time of the protocol.
	 * <p>
	 * The end time is the end time of the last {@link TracItem} in the protocol.
	 * </p>
	 * 
	 * @param protocol the protocol. Cannot be <code>null</code>.
	 * @return end time. <code>null</code> if none found.
	 */
	public TemporalAccessor getEndTime(TracProtocol protocol) {
		Objects.requireNonNull(protocol);

		List<TracItem> items = protocol.items();
		int size = items.size();
		return size > 0 ? items.get(size - 1).endTime() : null;
	}

	/**
	 * Returns the duration of the given protocol.
	 * 
	 * @param protocol the protocol. Cannot be <code>null</code>.
	 * @return duration. <code>null</code> if protocol doesn't have both a start
	 *         time and an end time.
	 */
	public Duration getDuration(TracProtocol protocol) {
		requireNonNull(protocol);

		final TemporalAccessor startTime = getStartTime(protocol);
		final TemporalAccessor endTime = getEndTime(protocol);

		if (startTime != null && endTime != null) {
			Duration duration = new DurationCalculator().calculateDuration(startTime, endTime);
			return duration;
		}
		return null;
	}

}
