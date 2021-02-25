package org.ingomohr.trac.model;

import java.time.temporal.TemporalAccessor;

/**
 * An item that represents a worklog entry in an {@link ITracProtocol}.
 * <p>
 * An item contains:
 * <ul>
 * <li>the <b>message</b> - the actual log message</li>
 * <li>a <b>start end end time</b></li>
 * </ul>
 * </p>
 */
public interface IWorklogItem extends ITracItem {

    /**
     * Returns the start time of the item.
     * 
     * @return start time. <code>null</code> if not set.
     */
    TemporalAccessor getStartTime();

    /**
     * Sets the start time of the item.
     * 
     * @param startTime the start time.
     */
    void setStartTime(TemporalAccessor startTime);

    /**
     * Returns the end time of the item.
     * 
     * @return end time. <code>null</code> if not set.
     */
    TemporalAccessor getEndTime();

    /**
     * Sets the end time of the item.
     * 
     * @param endTime end time.
     */
    void setEndTime(TemporalAccessor endTime);

    /**
     * Returns the message.
     * 
     * @return the message. <code>null</code> if not set.
     */
    String getMessage();

    /**
     * Sets the message.
     * 
     * @param message the message.
     */
    void setMessage(String message);

}
