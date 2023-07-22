package org.ingomohr.trac.in;

import static java.util.Objects.requireNonNull;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.util.TimeConverter;

/**
 * Standard implementation for {@link ITracReader}.
 * <p>
 * <ul>
 * <li>Considers empty lines as separators between protocols</li>
 * <li>Considers lines starting with 'hh:mm-hh:mm' items with start time and end
 * time</li>
 * <li>Considers lines starting with 'hh:mm' items with start time - and the end
 * time is the start time of the successor item</li>
 * <li>Considers the first line as document title</li>
 * <li>Considers "#" as start of a line comment</li>
 * <li>Considers "----" (lines of only dashes) as comment</li>
 * </ul>
 * </p>
 */
public class DefaultTracReader implements ITracReader {

	protected static final Pattern PATTERN_ITEM = Pattern
			.compile("([0-2][0-9]:[0-5][0-9])(-[0-2][0-9]:[0-5][0-9])?(.*)");

	protected static final Pattern PATTERN_LINE = Pattern.compile("(\\-)+");

	@Override
	public List<TracProtocol> read(String document) {
		requireNonNull(document, "Document cannot be null.");

		final List<TracProtocol> protocols = new ArrayList<TracProtocol>();

		if (!document.isEmpty()) {
			String[] lines = document.split(System.lineSeparator());

			List<String> protocolChunks = toProtocolChunks(lines);

			for (String chunk : protocolChunks) {
				TracProtocol protocol = readProtocol(chunk);
				protocols.add(protocol);
			}
		}

		return protocols;
	}

	/**
	 * Turns the given lines into protocol chunks. Each of the chunks contains all
	 * lines of one protocol.
	 * <p>
	 * The standard implementation considers every empty line as separator between
	 * protocols.
	 * </p>
	 * 
	 * @param lines the lines to turn into protocol chunks. Cannot be
	 *              <code>null</code>.
	 * @return protocol chunks. Never <code>null</code>, possibly empty.
	 */
	protected List<String> toProtocolChunks(String[] lines) {
		final List<String> chunks = new ArrayList<String>();

		StringBuilder builder = new StringBuilder();

		for (String line : requireNonNull(lines)) {

			if (line.trim().isEmpty()) {
				if (builder.length() > 0) {
					chunks.add(builder.toString());
					builder = new StringBuilder();
				}
			} else {
				builder.append(line).append(System.lineSeparator());
			}
		}

		if (builder.length() > 0) {
			chunks.add(builder.toString());
		}

		return chunks;
	}

	protected TracProtocol readProtocol(String document) {

		final TimeConverter timeConverter = new TimeConverter();

		TracItem predItem = null;

		TracProtocol protocol = null;

		if (!document.isEmpty()) {
			final String[] lines = document.split(System.lineSeparator());

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i].trim();

				// Cut off trailing comments from line
				int indexOfCommentChar = line.indexOf("#");
				if (indexOfCommentChar > 0) {
					line = line.substring(0, indexOfCommentChar);
				}

				final Matcher workLogItemMatcher = PATTERN_ITEM.matcher(line);
				final boolean lineIsWorkItem = workLogItemMatcher.matches();

				if (i == 0) {
					if (!lineIsWorkItem) {
						protocol = createProtocol(line);
						continue;
					} else {
						protocol = createProtocol(null);
					}
				}

				if (lineIsWorkItem) {
					TracItem item = readItem(workLogItemMatcher, line, timeConverter);
					protocol.items().add(item);

					if (predItem != null && predItem.endTime() == null) {
						TracItem newPredItem = withEndTime(predItem, item.startTime());
						replaceItem(protocol, predItem, newPredItem);
					}

					predItem = item;
				} else if (!line.startsWith("#") && !PATTERN_LINE.matcher(line).matches()) {
					throwCannotReadLine(line);
				}
			}
		}

		return protocol;
	}

	/**
	 * Reads the given line into a {@link TracItem}.
	 * 
	 * @param matcher       the matcher for the given line. Cannot be
	 *                      <code>null</code>.
	 * @param line          the line to read. Cannot be <code>null</code>.
	 * @param timeConverter the {@link TimeConverter} to use. Cannot be
	 *                      <code>null</code>.
	 */
	protected TracItem readItem(Matcher matcher, String line, TimeConverter timeConverter) {
		requireNonNull(matcher);
		requireNonNull(line);
		requireNonNull(timeConverter);

		int count = matcher.groupCount();
		if (count != 3) {
			throwCannotReadLine(line);
		}

		String start = matcher.group(1);
		TemporalAccessor startTime = timeConverter.toTime(start);

		TemporalAccessor endTime = null;
		String end = matcher.group(2);
		if (end != null && end.length() > 0) {
			end = end.substring(1);
			endTime = timeConverter.toTime(end);
		}

		String text = matcher.group(3).trim();

		return new TracItem(startTime, endTime, text);

	}

	/**
	 * Creates a new {@link TracProtocol}.
	 * 
	 * @param title the protocol title to set.
	 * @return new protocol. Never <code>null</code>.
	 */
	protected TracProtocol createProtocol(String title) {
		return new TracProtocol(title);
	}

	private void throwCannotReadLine(final String line) {
		throw new RuntimeException("Unsupported format: Cannot read line: '" + line + "'");
	}

	private TracItem withEndTime(TracItem item, TemporalAccessor endTime) {
		return new TracItem(item.startTime(), endTime, item.text());
	}

	private void replaceItem(TracProtocol protocol, TracItem item, TracItem newItem) {
		requireNonNull(item);
		requireNonNull(newItem);

		protocol.items().replaceAll((a) -> {
			if (a == item) {
				return newItem;
			}
			return a;
		});
	}

}
