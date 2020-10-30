package org.ingomohr.trac.in;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;
import org.ingomohr.trac.util.TimeDateConverter;

/**
 * Parser to read given raw protocol content into a protocol model.
 * <p>
 * A protocol is given as text:
 * 
 * <pre>
Fr
---
08:27 Topic A
08:44-08:57 Topic B
09:25-45 Topic C: Topic C1
 * 
 * </pre>
 * </p>
 * <p>
 * <ul>
 * <li>Each line represents one protocol entry</li>
 * <li>An entry starts with a starting time</li>
 * <li>Each line can optionally have an ending time. If not specify, the ending
 * time is the starting time of the follow-up entry.</li>
 * <li>Each entry can specify a topic-path by using <code>":"</code> as
 * separator</li>
 * <li><code>"Break"</code> is a special topic. It is reserved for breaks.
 * </ul>
 * </p>
 */
public class TracProtocolParser {

    private static final Pattern PATTERN_ITEM = Pattern.compile("([0-2][0-9]:[0-5][0-9])(-[0-2][0-9]:[0-5][0-9])?(.*)");

    /**
     * Parses a {@link TracProtocol} from a given document.
     * 
     * @param document the document to parse. Cannot be <code>null</code>.
     * @return new protocol. Never <code>null</code>.
     */
    public TracProtocol parse(String document) {
        Objects.requireNonNull(document, "Document cannot be null.");
        TracProtocol protocol = new TracProtocol();

        if (!document.isEmpty()) {
            String[] lines = document.split(System.lineSeparator());

            TracItem predecessorItem = null;

            String currentSectionTitle = null;

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();

                final Matcher matcher = PATTERN_ITEM.matcher(line);
                final boolean isItemLine = matcher.matches();

                if (!ignoreLine(line, isItemLine)) {
                    if (!isItemLine) {
                        currentSectionTitle = line.trim();
                        if (i == 0) {
                            protocol.setTitle(currentSectionTitle);
                        }
                    } else if (isItemLine) {
                        TracItem item = new TracItem();
                        protocol.getItems().add(item);
                        item.setProtocol(protocol);
                        item.setSectionTitle(currentSectionTitle);

                        item.setRawText(line);
                        parseItem(matcher, line, item, predecessorItem);

                        predecessorItem = item;
                    }
                }

            }
        }

        return protocol;
    }

    private boolean ignoreLine(String line, boolean isItemLine) {
        if (isItemLine) {
            return false;
        } else if ("".equals(line.trim())) {
            return true;
        } else if (line.startsWith("#")) {
            return true;
        } else if (Pattern.matches("-+", line)) {
            return true;
        }

        return false;
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
            TimeDateConverter converter = new TimeDateConverter();

            Date dateStart = converter.toDate(start);
            Date dateEnd = converter.toDate(end);

            if (dateEnd.before(dateStart)) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateEnd);
                cal.add(Calendar.DATE, 1); // add 1 day
                dateEnd = cal.getTime();
            }

            long diffInMillis = dateEnd.getTime() - dateStart.getTime();
            return (int) TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
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