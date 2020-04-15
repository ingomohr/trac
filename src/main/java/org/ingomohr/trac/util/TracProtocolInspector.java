package org.ingomohr.trac.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;

/**
 * Computes information from a {@link TracProtocol}.
 */
public class TracProtocolInspector {

    /**
     * Returns the start time of the protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return start time. <code>null</code> if none found.
     */
    public String getStartTime(TracProtocol protocol) {
        Objects.requireNonNull(protocol);

        final List<TracItem> items = protocol.getItems();
        if (items.size() > 0) {
            return items.get(0).getStartTime();
        }
        return null;
    }

    /**
     * Returns the end time of the protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return end time. <code>null</code> if none found.
     */
    public String getEndTime(TracProtocol protocol) {
        Objects.requireNonNull(protocol);

        final List<TracItem> items = protocol.getItems();
        final int size = items.size();
        if (size > 0) {
            return items.get(size - 1).getEndTime();
        }
        return null;
    }

    /**
     * Returns the total time spent for breaks - i.e. the sum of all items for a
     * top-level topic with name "break" (case-insensitive detection).
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return total time spent for breaks. In HH:mm. Never <code>null</code>.
     */
    public String getTimeSpentForBreaks(TracProtocol protocol) {
        Objects.requireNonNull(protocol);

        return getTimeSpent(protocol, it -> isBreak(it));
    }

    /**
     * Returns the total time spent in the entire protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return total time spent. In HH:mm. Never <code>null</code>.
     */
    public String getTimeSpentTotal(TracProtocol protocol) {
        Objects.requireNonNull(protocol);
        return getTimeSpent(protocol, it -> true);
    }

    /**
     * Returns the total time spent in the entire protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return total time spent. In minutes.
     */
    public int getTimeSpentTotalInMinutes(TracProtocol protocol) {
        Objects.requireNonNull(protocol);
        return getTimeSpentInMinutes(protocol, it -> true);
    }

    /**
     * Returns the total time spent in the entire protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @param filter   a filter to specify the items whose time to count. Cannot be
     *                 <code>null</code>.
     * @return total time spent - in minutes.
     */
    public int getTimeSpentInMinutes(TracProtocol protocol, Predicate<TracItem> filter) {
        Objects.requireNonNull(protocol);
        Objects.requireNonNull(filter);
        return protocol.getItems().stream().filter(filter).mapToInt(it -> it.getTimeSpentInMinutes()).sum();
    }

    public String getTimeSpentWithoutBreaks(TracProtocol protocol) {
        Objects.requireNonNull(protocol);
        return getTimeSpent(protocol, it -> !isBreak(it));
    }

    private String getTimeSpent(TracProtocol protocol, Predicate<TracItem> filter) {
        int minutes = getTimeSpentInMinutes(protocol, filter);
        return new TimeDateConverter().toHHmm(minutes);
    }

    private boolean isBreak(TracItem item) {
        final TracTopic topic = getTopLevelTopic(item);
        if (topic != null) {
            final String name = topic.getName();
            return name != null && name.toLowerCase().equals("break");
        }
        return false;
    }

    /**
     * Returns the top-level topic for the given item.
     * 
     * @param item the item. Cannot be <code>null</code>.
     * @return top-level topic for given item. <code>null</code> if none found.
     */
    public TracTopic getTopLevelTopic(TracItem item) {
        Objects.requireNonNull(item);

        TracTopic topic = item.getTopic();
        while (topic != null && topic.getParent() != null) {
            topic = topic.getParent();
        }

        return topic;
    }

}
