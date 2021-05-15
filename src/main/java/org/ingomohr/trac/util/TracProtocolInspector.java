package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.ITracItem;
import org.ingomohr.trac.model.ITracProtocol;
import org.ingomohr.trac.model.IWorklogItem;

/**
 * Computes information from an {@link ITracProtocol}.
 */
public class TracProtocolInspector {

    /**
     * Returns the start time of the protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return start time. <code>null</code> if none found.
     */
    public TemporalAccessor getStartTime(ITracProtocol protocol) {
        Objects.requireNonNull(protocol);

        final List<ITracItem> items = protocol.getItems();
        for (ITracItem item : items) {
            if (item instanceof IWorklogItem) {
                return ((IWorklogItem) item).getStartTime();
            }
        }
        return null;
    }

    /**
     * Returns the end time of the protocol.
     * 
     * @param protocol the protocol. Cannot be <code>null</code>.
     * @return end time. <code>null</code> if none found.
     */
    public TemporalAccessor getEndTime(ITracProtocol protocol) {
        Objects.requireNonNull(protocol);

        List<ITracItem> items = protocol.getItems();

        for (int i = items.size() - 1; i >= 0; i--) {
            ITracItem item = items.get(i);
            if (item instanceof IWorklogItem) {
                return ((IWorklogItem) item).getEndTime();
            }
        }

        return null;
    }

    /**
     * Returns the number of minutes the given protocol spans from start to end.
     * 
     * @param protocol the protocol to inspect. Cannot be <code>null</code>.
     * @return minutes from start to end. <code>-1<code> if not both start- and end
     *         time are available.
     */
    public int getTimeSpan(ITracProtocol protocol) {
        requireNonNull(protocol);

        TemporalAccessor startTime = getStartTime(protocol);
        TemporalAccessor endTime = getEndTime(protocol);
        return new TimeDiffCalculator().getDiffInMinutes(startTime, endTime);
    }

}
