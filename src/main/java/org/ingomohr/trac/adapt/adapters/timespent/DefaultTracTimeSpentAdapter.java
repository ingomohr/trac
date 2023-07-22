package org.ingomohr.trac.adapt.adapters.timespent;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.TracItemInspector;
import org.ingomohr.trac.util.TracProtocolInspector;

/**
 * Default implementation of {@link TracTimeSpentAdapter}.
 */
public class DefaultTracTimeSpentAdapter implements TracTimeSpentAdapter {

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

		for (int i = 0, n = items.size(); i < n; i++) {
			TracItem item = items.get(i);

			TracItemInspector inspector = new TracItemInspector();
			final Duration dur = inspector.getDuration(item);
			String text = item.text();

			if (text.contains("#")) {
				String textWithoutComment = text.substring(0, text.indexOf("#"));
				textWithoutComment = textWithoutComment.trim();
				text = textWithoutComment;
			}

			if (!("break".equalsIgnoreCase(text)) && !text.isEmpty()) {
				TemporalAccessor startTime = item.startTime();
				TemporalAccessor endTime = item.endTime();

				if (startTime == null || endTime == null) {
					throw new RuntimeException(
							"Cannot compute duration. Item must have both start- and end time: " + item);
				}

				if (overAllDuration == null) {
					overAllDuration = dur;
				} else {
					overAllDuration = overAllDuration.plusMinutes(dur.toMinutes());
				}
			}
		}

		return overAllDuration;
	}

}
