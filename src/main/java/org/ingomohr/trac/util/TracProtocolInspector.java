package org.ingomohr.trac.util;

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
                return ((IWorklogItem) item).getStartTime();
            }
        }

        return null;
    }

}
