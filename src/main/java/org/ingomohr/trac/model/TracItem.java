package org.ingomohr.trac.model;

/**
 * TracItem represents one item in a trac protocol.
 * <p>
 * An item contains:
 * <ul>
 * <li>a raw text - which is the source information of all other information
 * stored in the item</li>
 * <li>a start end end time</li>
 * <li>the number of minutes spent on the item (this is the diff between start
 * time and end time)</li>
 * <li>the corresponding topic to which the item belongs</li>
 * <li>a section-title. If a protocol consists of multiple smaller sections with
 * a title each, each item has its section's title.</li>
 * </ul>
 * </p>
 */
public class TracItem {

    private String sectionTitle;
    private String startTime;
    private String endTime;
    private String rawText;
    private int timeSpentInMinutes;
    private TracTopic topic;
    private TracProtocol protocol;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public int getTimeSpentInMinutes() {
        return timeSpentInMinutes;
    }

    public void setTimeSpentInMinutes(int timeSpentInMinutes) {
        this.timeSpentInMinutes = timeSpentInMinutes;
    }

    public TracTopic getTopic() {
        return topic;
    }

    public void setTopic(TracTopic topic) {
        this.topic = topic;
    }

    public TracProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(TracProtocol protocol) {
        this.protocol = protocol;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((rawText == null) ? 0 : rawText.hashCode());
        result = prime * result + ((sectionTitle == null) ? 0 : sectionTitle.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        result = prime * result + timeSpentInMinutes;
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TracItem other = (TracItem) obj;
        if (endTime == null) {
            if (other.endTime != null)
                return false;
        } else if (!endTime.equals(other.endTime))
            return false;
        if (protocol == null) {
            if (other.protocol != null)
                return false;
        } else if (!protocol.equals(other.protocol))
            return false;
        if (rawText == null) {
            if (other.rawText != null)
                return false;
        } else if (!rawText.equals(other.rawText))
            return false;
        if (sectionTitle == null) {
            if (other.sectionTitle != null)
                return false;
        } else if (!sectionTitle.equals(other.sectionTitle))
            return false;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        if (timeSpentInMinutes != other.timeSpentInMinutes)
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TracItem [sectionTitle=" + sectionTitle + ", startTime=" + startTime + ", endTime=" + endTime
                + ", rawText=" + rawText + ", timeSpentInMinutes=" + timeSpentInMinutes + ", topic=" + topic
                + ", protocol=" + protocol + "]";
    }

}