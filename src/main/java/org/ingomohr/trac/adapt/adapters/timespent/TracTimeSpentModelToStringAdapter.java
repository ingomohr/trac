package org.ingomohr.trac.adapt.adapters.timespent;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.adapt.ModelToStringAdapter;
import org.ingomohr.trac.util.DurationToStringConverter;
import org.ingomohr.trac.util.TimeConverter;

/**
 * Adapts a {@link TracTimeSpentModel} to a String representation.
 */
public class TracTimeSpentModelToStringAdapter implements ModelToStringAdapter<TracTimeSpentModel> {

	/**
	 * Returns a String-representation for the given model.
	 * 
	 * @param model the model to compute the string for. Cannot be
	 *              <code>null</code>.
	 * @return string representing the model. Never <code>null</code>.
	 */
	public String adapt(TracTimeSpentModel model) {
		Objects.requireNonNull(model);

		StringBuilder builder = new StringBuilder();
		builder.append("# Time spent").append(System.lineSeparator());

		String totalEffectiveDuration = calculateTotalDuration(model);
		builder.append("Total (EWT): " + totalEffectiveDuration);
		builder.append(System.lineSeparator());

		if (!model.entries().isEmpty()) {

			final int numEntries = model.entries().size();

			String[][] entries = computeEntriesToPrint(model);
			int[] colWidths = computeColumnWidths(entries);
			int colWidthForCounter = String.valueOf(numEntries).length() + 2;

			int counter = 0;

			for (String[] entry : entries) {

				final int numAdditionalChars = 11;

				if (counter % 5 == 0) {
					builder.append(mkSeparatorLine(colWidthForCounter + numAdditionalChars, colWidths));
					builder.append(System.lineSeparator());
				}

				String title = entry[0];
				String start = entry[1];
				String end = entry[2];
				String effectiveDuration = entry[3];

				builder.append(mkEntryForColumnAlignRight(++counter, colWidthForCounter));
				builder.append(". ");

				builder.append(mkEntryForColumnAlignLeft(title, colWidths[0]));
				builder.append(mkEntryForColumnAlignLeft(start, colWidths[1]));
				builder.append("- ");
				builder.append(mkEntryForColumnAlignLeft(end, colWidths[2]));
				builder.append("-- EWT:");
				builder.append(mkEntryForColumnAlignRight(effectiveDuration, colWidths[3]));
				builder.append(System.lineSeparator());
			}

			builder.append(System.lineSeparator());
			builder.append("EWT: Effective working time (i.e. w/o breaks)").append(System.lineSeparator());

		} else {
			builder.append("no information found").append(System.lineSeparator());
		}

		return builder.toString();
	}

	private String calculateTotalDuration(TracTimeSpentModel model) {
		Duration totalDuration = null;

		List<TracTimeSpentModelEntry> entries = model.entries();
		for (TracTimeSpentModelEntry tracTimeSpentModelEntry : entries) {
			Duration duration = tracTimeSpentModelEntry.effectiveDuration();

			if (totalDuration == null) {
				totalDuration = duration;
			} else if (duration != null) {
				totalDuration = totalDuration.plus(duration);
			}
		}

		if (totalDuration != null) {
			return new DurationToStringConverter().toString(totalDuration);
		}

		return "-";
	}

	private String mkSeparatorLine(int initialWidth, int[] colWidths) {
		int width = initialWidth;
		for (int i = 0; i < colWidths.length; i++) {
			width += colWidths[i];
		}

		StringBuilder builder = new StringBuilder();

		while (width > 0) {
			builder.append("-");
			width--;
		}

		return builder.toString();
	}

	private String mkEntryForColumnAlignRight(int content, int colWidthForCounter) {
		String text = String.valueOf(content);

		text = alignRight(colWidthForCounter, text);

		return text;
	}

	private String alignRight(int colWidthForCounter, String text) {
		int spacesToPrepend = colWidthForCounter - text.length();
		while (spacesToPrepend > 0) {
			text = " " + text;
			spacesToPrepend--;
		}
		return text;
	}

	private String mkEntryForColumnAlignRight(String content, int colWidthForCounter) {
		String text = content;

		text = alignRight(colWidthForCounter, text);

		return text;
	}

	private String mkEntryForColumnAlignLeft(String content, int colWidthForCounter) {
		String text = content;

		int spacesToAppend = colWidthForCounter - text.length();
		while (spacesToAppend > 0) {
			text = text + " ";
			spacesToAppend--;
		}

		return text;
	}

	private String[][] computeEntriesToPrint(TracTimeSpentModel model) {

		String[][] entries = new String[model.entries().size()][4];

		int i = 0;
		for (TracTimeSpentModelEntry entry : model.entries()) {
			String title = adaptProtocolTitle(entry);
			String start = adaptStartTime(entry);
			String end = adaptEndTime(entry);
			String effectiveDuration = adaptEffectiveDuration(entry);

			entries[i][0] = title;
			entries[i][1] = start;
			entries[i][2] = end;
			entries[i][3] = effectiveDuration;

			i++;
		}

		return entries;
	}

	private int[] computeColumnWidths(String[][] entries) {

		int[] columnWidths = new int[4];

		for (String[] entry : entries) {
			String title = entry[0];
			String start = entry[1];
			String end = entry[2];
			String effectiveDuration = entry[3];

			int w0 = computeWidth(title);
			int w1 = computeWidth(start);
			int w2 = computeWidth(end);
			int w3 = computeWidth(effectiveDuration);

			columnWidths[0] = Math.max(columnWidths[0], w0);
			columnWidths[1] = Math.max(columnWidths[1], w1);
			columnWidths[2] = Math.max(columnWidths[2], w2);
			columnWidths[3] = Math.max(columnWidths[3], w3);
		}

		return columnWidths;
	}

	private int computeWidth(String text) {
		return text != null ? text.length() + 1 : 0;
	}

	private String adaptEffectiveDuration(TracTimeSpentModelEntry entry) {
		if (entry.effectiveDuration() != null) {
			return new DurationToStringConverter().toString(entry.effectiveDuration());
		}

		return "<no duration>";
	}

	private String adaptEndTime(TracTimeSpentModelEntry entry) {
		return entry.endTime() != null ? new TimeConverter().toHHmm(entry.endTime()) : "<no end time>";
	}

	private String adaptStartTime(TracTimeSpentModelEntry entry) {
		return entry.startTime() != null ? new TimeConverter().toHHmm(entry.startTime()) : "<no start time>";
	}

	private String adaptProtocolTitle(TracTimeSpentModelEntry entry) {
		String title = entry.protocol() != null ? entry.protocol().title() : null;
		title = title == null ? "<no title>" : title;
		return title;
	}

}
