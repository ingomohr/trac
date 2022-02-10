package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;

/**
 * Computes information on an {@link TracProtocol}.
 */
public class TracProtocolInspector {

    /**
     * Returns the start time of the protocol.
     * <p>
     * The start time is the start time of the first {@link TracItem} in the
     * protocol.
     * </p>
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return start time. <code>null</code> if none found.
     */
    public TemporalAccessor getStartTime(TracProtocol protocol) {
        Objects.requireNonNull(protocol);

        TracItem firstItem = protocol.items().stream().findFirst().orElse(null);
        if (firstItem != null) {
            return firstItem.startTime();
        }
        return null;
    }

    /**
     * Returns the end time of the protocol.
     * <p>
     * The end time is the end time of the last {@link TracItem} in the protocol.
     * </p>
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return end time. <code>null</code> if none found.
     */
    public TemporalAccessor getEndTime(TracProtocol protocol) {
        Objects.requireNonNull(protocol);

        List<TracItem> items = protocol.items();
        int size = items.size();
        return size > 0 ? items.get(size - 1).endTime() : null;
    }

    /**
     * Returns the number of minutes the given protocol spans from start to end.
     * 
     * @param protocol the protocol to inspect. Cannot be <code>null</code>.
     * @return minutes from start to end. <code>-1<code> if not both start- and end
     *         time are available.
     */
    public int getTimeSpanInMinutes(TracProtocol protocol) {
        requireNonNull(protocol);

        TemporalAccessor startTime = getStartTime(protocol);
        TemporalAccessor endTime = getEndTime(protocol);

        if (startTime != null && endTime != null) {
            return new TimeDiffCalculator().getDiffInMinutes(startTime, endTime);
        }
        return -1;
    }

}
