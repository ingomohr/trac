package org.ingomohr.trac.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TracTopic represents a topic a {@link TracItem} belongs to.
 * <p>
 * Topics can be arranged in trees: Each topic can have a number of sub-topics
 * and a parent topic.
 * </p>
 */
public class TracTopic {

    private String name;

    private TracTopic parent;

    private final List<TracTopic> children = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TracTopic getParent() {
        return parent;
    }

    public void setParent(TracTopic parent) {
        this.parent = parent;
    }

    public List<TracTopic> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "TracTopic [children=" + children + ", name=" + name + ", parent=" + parent + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
        TracTopic other = (TracTopic) obj;
        if (children == null) {
            if (other.children != null)
                return false;
        } else if (!children.equals(other.children))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        return true;
    }

}