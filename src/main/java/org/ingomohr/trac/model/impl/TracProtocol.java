package org.ingomohr.trac.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.ITracItem;
import org.ingomohr.trac.model.ITracProtocol;

/**
 * Standard impl of {@link ITracProtocol}
 */
public class TracProtocol implements ITracProtocol {

    private String title;

    private final List<ITracItem> items = new ArrayList<>();

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<ITracItem> getItems() {
        return items;
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TracProtocol)) {
            return false;
        }
        TracProtocol other = (TracProtocol) obj;
        return Objects.equals(items, other.items) && Objects.equals(title, other.title);
    }

    @Override
    public String toString() {
        return "TracProtocol [title=" + title + ", items=" + items + "]";
    }

}