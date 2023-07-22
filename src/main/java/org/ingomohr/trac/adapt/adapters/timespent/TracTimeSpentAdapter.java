package org.ingomohr.trac.adapt.adapters.timespent;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.adapt.ITracAdapter;
import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.TimeConverter;
import org.ingomohr.trac.util.TracProtocolInspector;

/**
 * For each protocol, this provides the following information:
 * <ul>
 * <li>Starting time of each protocol</li>
 * <li>Ending time of each protocol</li>
 * <li>Sum of time spent ({@link org.ingomohr.trac.model.TracItem}s with a text
 * of "Break" (ignore case) are ignored)
 * </ul>
 */
public class TracTimeSpentAdapter implements ITracAdapter<TracTimeSpentModel> {

	@Override
	public TracTimeSpentModel adapt(List<TracProtocol> protocols) {
		TracTimeSpentModel model = new TracTimeSpentModel(new ArrayList<>());
		adapt(protocols, model);
		return model;
	}

	/**
	 * Adapts the given protocols into the given target model.
	 * 
	 * @param protocols the protocols to adapt. Cannot be <code>null</code>.
	 * @param target    the target model. Cannot be <code>null</code>.
	 */
	protected void adapt(List<TracProtocol> protocols, TracTimeSpentModel target) {
		Objects.requireNonNull(protocols);
		Objects.requireNonNull(target);

		final TracProtocolInspector inspector = new TracProtocolInspector();

		for (TracProtocol prot : protocols) {
			TemporalAccessor startTime = inspector.getStartTime(prot);
			TemporalAccessor endTime = inspector.getEndTime(prot);

			Duration timeSpent = computeTimeSpentWithoutBreaks(prot.items());

			TracTimeSpentModelEntry entry = new TracTimeSpentModelEntry(prot, startTime, endTime, timeSpent);

			target.entries().add(entry);
		}
	}

	/**
	 * Returns the time spent without breaks from the given items (in their given
	 * order).
	 * <p>
	 * Items with text "Break" (ignore case) are ignored.
	 * </p>
	 * 
	 * @param items the items to compute for. Cannot be <code>null</code>.
	 * @return time spent. <code>null</code> if no starting time <i>and</i> ending
	 *         time have been found.
	 * @throws RuntimeException if there are insufficient time infos to computed the
	 *                          duration.
	 */
	protected Duration computeTimeSpentWithoutBreaks(List<TracItem> items) {
		Duration overAllDuration = null;

		final TimeConverter timeConverter = new TimeConverter();

		for (int i = 0, n = items.size(); i < n; i++) {
			TracItem item = items.get(i);

			if (!"break".equalsIgnoreCase(item.text())) {
				TemporalAccessor startTime = item.startTime();
				TemporalAccessor endTime = item.endTime();

				if (startTime == null || endTime == null) {
					throw new RuntimeException(
							"Cannot compute duration. Item must have both start- and end time: " + item);
				}

				Duration duration = computeItemDuration(startTime, endTime, timeConverter);
				if (overAllDuration == null) {
					overAllDuration = duration;
				} else {
					overAllDuration = overAllDuration.plusMinutes(duration.toMinutes());
				}
			}
		}

		return overAllDuration;
	}

	private Duration computeItemDuration(TemporalAccessor startTime, TemporalAccessor endTime,
			TimeConverter timeConverter) {
		int hours1 = timeConverter.getHours(startTime);
		int mins1 = timeConverter.getMinutes(startTime);
		int hours2 = timeConverter.getHours(endTime);
		int mins2 = timeConverter.getMinutes(endTime);

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
