package org.ingomohr.trac;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;

/**
 * Parser to read given raw protocol content into a protocol model.
 */
public class TracParser {

	private static final Pattern PATTERN_ITEM = Pattern.compile("([0-2][0-9]:[0-5][0-9])(-[0-2][0-9]:[0-5][0-9])?(.*)");

	/**
	 * Parses a {@link TracProtocol} from a given document.
	 * 
	 * @param pDocument the document to parse. Cannot be <code>null</code>.
	 * @return new protocol. Never <code>null</code>.
	 */
	public TracProtocol parse(String pDocument) {
		Objects.requireNonNull(pDocument, "Document cannot be null.");
		TracProtocol protocol = new TracProtocol();

		if (!pDocument.isEmpty()) {
			String[] lines = pDocument.split(System.lineSeparator());

			TracItem predecessorItem = null;

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i].trim();

				final Matcher matcher = PATTERN_ITEM.matcher(line);
				final boolean isItemLine = matcher.matches();

				if (i == 0 && !isItemLine) {
					protocol.setTitle(line.trim());
				} else if (isItemLine) {
					TracItem item = new TracItem();
					protocol.getItems().add(item);
					item.setProtocol(protocol);

					item.setRawText(line);
					parseItem(matcher, line, item, predecessorItem);

					predecessorItem = item;

				}

			}
		}

		return protocol;
	}

	private void parseItem(Matcher matcher, String line, TracItem target, TracItem predecessorItem) {

		int count = matcher.groupCount();

		switch (count) {
		case 3:
			String start = matcher.group(1);
			String end = matcher.group(2);
			String payload = matcher.group(3);

			parseStartTime(start, target);
			parseEndTime(end, target);
			parsePayload(payload, target);
			break;

		default:
			throw new RuntimeException("Unsupported match: Cannot read line: '" + line + "'");
		}

		target.setTimeSpentInMinutes(computeMinutesSpent(target));

		if (predecessorItem != null && predecessorItem.getEndTime() == null) {
			predecessorItem.setEndTime(target.getStartTime());
			predecessorItem.setTimeSpentInMinutes(computeMinutesSpent(predecessorItem));
		}
	}

	private int computeMinutesSpent(TracItem item) {
		String start = item.getStartTime();
		String end = item.getEndTime();

		if (start != null && end != null) {
			String pattern = "HH:mm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			Date dateStart;
			Date dateEnd;
			try {
				dateStart = simpleDateFormat.parse(start);
				dateEnd = simpleDateFormat.parse(end);
				long diffInMillis = dateEnd.getTime() - dateStart.getTime();
				return (int) TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
			} catch (ParseException e) {
				throw new RuntimeException("Cannot read time stamp.", e);
			}
		}

		return -1;
	}

	private void parsePayload(String payload, TracItem target) {
		if (payload != null && !payload.isEmpty()) {

			final String trimmedPayload = payload.trim();

			final String[] payloadAndComment = trimmedPayload.split("//");

			final String[] segments = payloadAndComment[0].split(":");

			TracTopic parentTopic = null;

			for (int i = 0; i < segments.length; i++) {

				final String topicName = segments[i].trim();

				TracTopic topic = null;
				Collection<TracTopic> topicsList = null;

				if (i == 0) {
					topicsList = target.getProtocol().getTopics();
				} else {
					Objects.requireNonNull(parentTopic);
					topicsList = parentTopic.getChildren();
				}

				topic = getTopic(topicsList, topicName);

				if (topic == null) {
					topic = new TracTopic();
					topic.setName(topicName);

					topicsList.add(topic);
					topic.setParent(parentTopic);
				}

				target.setTopic(topic);
				parentTopic = topic;
			}

		}

	}

	private TracTopic getTopic(Collection<TracTopic> topics, String name) {
		return topics.stream().filter(tp -> Objects.equals(name, tp.getName())).findFirst().orElse(null);

	}

	private void parseEndTime(String endTime, TracItem target) {
		if (endTime != null && !endTime.isEmpty()) {
			target.setEndTime(endTime.substring(1));
		}
	}

	private void parseStartTime(String group, TracItem target) {
		target.setStartTime(group);
	}

}