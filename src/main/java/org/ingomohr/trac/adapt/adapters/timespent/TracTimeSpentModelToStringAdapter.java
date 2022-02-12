package org.ingomohr.trac.adapt.adapters.timespent;

import java.util.Objects;

import org.ingomohr.trac.util.DurationToStringConverter;
import org.ingomohr.trac.util.TimeConverter;

/**
 * Adapts a {@link TracTimeSpentModel} to a String representation.
 */
public class TracTimeSpentModelToStringAdapter {

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

        int counter = 1;
        if (!model.entries().isEmpty()) {

            for (TracTimeSpentModelEntry entry : model.entries()) {
                String title = adaptProtocolTitle(entry);
                String start = adaptStartTime(entry);
                String end = adaptEndTime(entry);
                String effectiveDuration = adaptEffectiveDuration(entry);

                builder.append(counter++).append(". ").append(title).append(" (").append(start).append("-").append(end)
                        .append(") - EWT: ").append(effectiveDuration).append(System.lineSeparator());
            }

            builder.append(System.lineSeparator());
            builder.append("EWT: Effective working time (i.e. w/o breaks)").append(System.lineSeparator());
        } else {
            builder.append("no information found").append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String adaptEffectiveDuration(TracTimeSpentModelEntry entry) {
        if (entry.timeSpent() != null) {
            return new DurationToStringConverter().toString(entry.timeSpent());
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
