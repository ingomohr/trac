package org.ingomohr.trac.model.impl;

import java.time.temporal.TemporalAccessor;
import java.util.Objects;

import org.ingomohr.trac.model.IWorklogItem;

/**
 * Standard implementation of {@link IWorklogItem}
 */
public class WorklogItem extends TracItem implements IWorklogItem {

    private TemporalAccessor startTime;

    private TemporalAccessor endTime;

    private String message;

    @Override
    public TemporalAccessor getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(TemporalAccessor startTime) {
        this.startTime = startTime;
    }

    @Override
    public TemporalAccessor getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(TemporalAccessor endTime) {
        this.endTime = endTime;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(endTime, message, startTime);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof WorklogItem)) {
            return false;
        }
        WorklogItem other = (WorklogItem) obj;
        return Objects.equals(endTime, other.endTime) && Objects.equals(message, other.message)
                && Objects.equals(startTime, other.startTime);
    }

    @Override
    public String toString() {
        return "WorklogItem [startTime=" + startTime + ", endTime=" + endTime + ", message=" + message + "]";
    }

}
