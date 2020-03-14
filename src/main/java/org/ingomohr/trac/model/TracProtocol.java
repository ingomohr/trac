package org.ingomohr.trac.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TracProtocol contains a list of {@link TracItem}s and a list of
 * {@link TracTopic}s to which the items belong.
 */
public class TracProtocol {

    private String title;

    private final List<TracItem> items = new ArrayList<>();

    private final List<TracTopic> topics = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TracItem> getItems() {
        return items;
    }

    public List<TracTopic> getTopics() {
        return topics;
    }

    @Override
    public String toString() {
        return "TracProtocol [items=" + items + ", title=" + title + ", topics=" + topics + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((topics == null) ? 0 : topics.hashCode());
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
        TracProtocol other = (TracProtocol) obj;
        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (topics == null) {
            if (other.topics != null)
                return false;
        } else if (!topics.equals(other.topics))
            return false;
        return true;
    }

}