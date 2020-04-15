package org.ingomohr.trac.out;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;
import org.ingomohr.trac.util.TimeDateConverter;
import org.ingomohr.trac.util.TracProtocolInspector;

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

        List<AnalyzedEntry> entries = new ArrayList<>(protocol.getTopics().size());

        protocol.getItems().forEach(item -> updateEntriesList(item, entries));

        entries.sort((e1, e2) -> e2.totalMinutesSpent - e1.totalMinutesSpent);

        writeEntries(out, protocol, entries);
    }

    private void writeEntries(PrintStream out, TracProtocol protocol, List<AnalyzedEntry> entries) {

        final TracProtocolInspector inspector = new TracProtocolInspector();
        final String startTime = inspector.getStartTime(protocol);
        final String endTime = inspector.getEndTime(protocol);
        final String timeTotal = inspector.getTimeSpentTotal(protocol);
        final String timeSpentForBreaks = inspector.getTimeSpentForBreaks(protocol);
        final String timeSpentWithoutBreaks = inspector.getTimeSpentWithoutBreaks(protocol);

        out.println("Protocol by Topics");

        writeHeaderSeparator(out);
        writeHeaderEntry(out, "Start           :", startTime);
        writeHeaderEntry(out, "End             :", endTime);
        writeHeaderSeparator(out);

        writeHeaderEntry(out, "Total time      :", timeTotal);
        writeHeaderEntry(out, "Breaks          :", timeSpentForBreaks);
        writeHeaderEntry(out, "Time w/o breaks :", timeSpentWithoutBreaks);

        writeHeaderSeparator(out);

        final int timeSpentTotalinMinutes = inspector.getTimeSpentTotalInMinutes(protocol);
        entries.forEach(entry -> {
            out.println(toEntryLine(entry, timeSpentTotalinMinutes));
        });

    }

    private void writeHeaderEntry(PrintStream out, String name, String hhmm) {
        String val = alignRight(hhmm, 6);
        out.println(name + val);
    }

    private String alignRight(String value, int totalLengthOfValueSlot) {
        int n = totalLengthOfValueSlot - value.length();

        final StringBuilder stringBuilder = new StringBuilder(value);

        while (n > 0) {
            stringBuilder.insert(0, " ");
            n--;
        }

        return stringBuilder.toString();
    }

    private void writeHeaderSeparator(PrintStream out) {
        out.println("-----------------------");
    }

    private String toEntryLine(AnalyzedEntry entry, int allMinutes) {

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

    private String getInfoTimeSpent(int minutesSpent, int totalMinutesInProtocol) {
        TimeDateConverter converter = new TimeDateConverter();
        String time = converter.toHHmm(minutesSpent);
        String total = converter.toHHmm(totalMinutesInProtocol);

        StringBuilder builder = new StringBuilder();
        builder.append(time);

        int n = total.length() - time.length();
        while (n > 0) {
            builder.insert(0, " ");
            n--;
        }

        return builder.toString();
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

    private void updateEntriesList(TracItem item, List<AnalyzedEntry> entries) {
        TracTopic topLevelTopic = new TracProtocolInspector().getTopLevelTopic(item);

        AnalyzedEntry entry = entries.stream().filter(e -> e.topic == topLevelTopic).findFirst().orElse(null);

        if (entry == null) {
            entry = new AnalyzedEntry();
            entry.topic = topLevelTopic;
            entries.add(entry);
        }

        entry.totalMinutesSpent += item.getTimeSpentInMinutes();
        entry.items.add(item);

        item.getStartTime();
    }

    private static class AnalyzedEntry {

        int totalMinutesSpent;

        TracTopic topic;

        List<TracItem> items = new ArrayList<>();

    }

}
