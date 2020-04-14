package org.ingomohr.trac.out;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;

/**
 * Writes a {@link TracProtocol}'s topics ordered by time spent.
 */
public class TracProtocolWriterByTopic {

    /**
     * Writes the topics of the given {@link TracProtocol} ordered by time spent
     * (desc).
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @param out      the stream to write to. Cannot be <code>null</code>.
     */
    public void write(TracProtocol protocol, PrintStream out) {
	Objects.requireNonNull(protocol);
	Objects.requireNonNull(out);

	List<Entry> entries = new ArrayList<>(protocol.getTopics().size());

	protocol.getItems().forEach(item -> updateEntriesList(item, entries));

	entries.sort((e1, e2) -> e2.totalMinutesSpent - e1.totalMinutesSpent);

	writeEntries(out, entries);
    }

    private void writeEntries(PrintStream out, List<Entry> entries) {
	out.println("Protocol by Topics");
	out.println("------------------");

	int allMinutes = entries.stream().mapToInt(e -> e.totalMinutesSpent).sum();
	out.println("Total time spent: " + getInfoTimeSpent(allMinutes, allMinutes));
	out.println();

	entries.forEach(entry -> {
	    out.println(toEntryLine(entry, allMinutes));
	});
    }

    private String toEntryLine(Entry entry, int allMinutes) {

	int minutes = entry.totalMinutesSpent;

	final int percentage = Math.round((minutes / (float) allMinutes) * 100f);

	StringBuilder builder = new StringBuilder();

	builder.append(getInfoTimeSpent(entry.totalMinutesSpent, allMinutes));
	builder.append(" ");
	builder.append(getInfoProgressBar(percentage));
	builder.append(" ");
	builder.append(getInfoPercentage(percentage));
	builder.append(" ");
	builder.append(entry.topic.getName());

	return builder.toString();
    }

    private String getInfoPercentage(int percentage) {
	final String val = String.valueOf(percentage);
	StringBuilder builder = new StringBuilder();

	builder.append(val);

	while (builder.length() < 3) {
	    builder.insert(0, " ");
	}

	builder.append("%");

	return builder.toString();
    }

    private String getInfoTimeSpent(int totalMinutesSpent, int allMinutes) {
	return toHoursInfo(totalMinutesSpent);
    }

    private String toHoursInfo(int minutes) {
	int hours = minutes / 60;
	int min = minutes % 60;
	final String format = String.format("%d:%02d", hours, min);
	return format;
    }

    private String getInfoProgressBar(final int percentage) {
	int numSlots = 20;
	int takenPercentage = percentage;

	StringBuilder builder = new StringBuilder();
	while (takenPercentage >= 5) {
	    builder.append("#");
	    takenPercentage -= 5;
	    numSlots--;
	}

	while (numSlots > 0) {
	    builder.append(".");
	    numSlots--;
	}
	return builder.toString();
    }

    private void updateEntriesList(TracItem item, List<Entry> entries) {
	TracTopic topLevelTopic = getTopLevelTopic(item);

	Entry entry = entries.stream().filter(e -> e.topic == topLevelTopic).findFirst().orElse(null);
	if (entry == null) {
	    entry = new Entry();
	    entry.topic = topLevelTopic;
	    entries.add(entry);
	}
	entry.totalMinutesSpent += item.getTimeSpentInMinutes();
	entry.items.add(item);
    }

    private TracTopic getTopLevelTopic(TracItem item) {
	TracTopic topic = item.getTopic();
	while (topic != null && topic.getParent() != null) {
	    topic = topic.getParent();
	}

	return topic;
    }

    private static class Entry {

	int totalMinutesSpent;

	TracTopic topic;

	List<TracItem> items = new ArrayList<>();

    }

}
